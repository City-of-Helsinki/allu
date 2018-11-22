import {Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Customer} from '@model/customer/customer';
import {FormBuilder, FormGroup} from '@angular/forms';
import {CodeSetCodeMap} from '@model/codeset/codeset';
import {BehaviorSubject, Observable, Subject} from 'rxjs/index';
import {debounceTime, filter, map, switchMap, take, takeUntil} from 'rxjs/internal/operators';
import {SearchByType} from '@feature/customerregistry/actions/customer-search-actions';
import {ArrayUtil} from '@util/array-util';
import {CustomerType} from '@model/customer/customer-type';
import {CustomerNameSearchMinChars, REGISTRY_KEY_SEARCH_MIN_CHARS} from '@service/customer/customer-search-query';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {MatDialog} from '@angular/material';
import {CUSTOMER_MODAL_CONFIG, CustomerModalComponent} from '@feature/information-request/acceptance/customer/customer-modal.component';
import {isEqualWithSkip} from '@util/object.util';
import {CustomerInfoAcceptanceComponent} from '@feature/information-request/acceptance/customer/customer-info-acceptance.component';

export abstract class CustomerAcceptanceComponent implements OnInit, OnDestroy {

  @Input() oldCustomer: Customer;
  @Input() newCustomer: Customer;
  @Input() parentForm: FormGroup;
  @Input() readonly: boolean;

  @ViewChild(CustomerInfoAcceptanceComponent) infoAcceptance: CustomerInfoAcceptanceComponent;

  referenceCustomer$: BehaviorSubject<Customer> = new BehaviorSubject<Customer>(undefined);

  form: FormGroup;
  searchForm: FormGroup;

  countryCodes$: Observable<CodeSetCodeMap>;
  matchingCustomers$: Observable<Customer[]>;

  protected formName = 'customer';
  protected actionTargetType = ActionTargetType.Customer;

  protected destroy = new Subject<boolean>();

  protected constructor(
    protected fb: FormBuilder,
    protected store: Store<fromRoot.State>,
    protected dialog: MatDialog) {}

  ngOnInit(): void {
    this.form = this.fb.group({});
    this.parentForm.addControl(this.formName, this.form);
    this.searchForm = this.fb.group({
      search: undefined
    });
    this.countryCodes$ = this.store.select(fromRoot.getCodeSetCodeMap('Country'));

    this.searchForm.get('search').valueChanges.pipe(
      takeUntil(this.destroy),
      debounceTime(300),
      filter(CustomerNameSearchMinChars)
    ).subscribe(term => this.searchCustomer(CustomerType[this.newCustomer.type], term, term));
    this.matchingCustomers$ = this.getMatchingCustomers();

    this.initialSearch();
    this.init();
  }

  // Inheriting components can override for initialization
  init(): void {}

  ngOnDestroy(): void {
    this.parentForm.removeControl(this.formName);
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  abstract customerChanges(customer: Customer): void;

  abstract getMatchingCustomers(): Observable<Customer[]>;

  abstract getLoading(): Observable<boolean>;

  selectReferenceCustomer(customer?: Customer): void {
    const search = customer ? `${customer.name} (${customer.registryKey})` : undefined;
    this.searchForm.patchValue({search}, {emitEvent: false});
    this.referenceCustomer$.next(customer);
  }

  createNewCustomer(): void {
    const config = {
      ...CUSTOMER_MODAL_CONFIG,
      data: {customer: this.newCustomer}
    };
    this.dialog.open(CustomerModalComponent, config).afterClosed().pipe(
      filter(customer => !!customer)
    ).subscribe(customer => {
      this.selectReferenceCustomer(customer);
      this.newCustomer = customer;
      this.oldCustomer = customer;
    });
  }

  get showCreateNew(): Observable<boolean> {
    return this.referenceCustomer$.pipe(
      map(ref => !isEqualWithSkip(ref, this.newCustomer, ['id', 'sapCustomerNumber']))
    );
  }

  private initialSearch() {
    this.searchCustomer(
      CustomerType[this.newCustomer.type],
      this.newCustomer.name,
      this.newCustomer.registryKey
    );

    if (this.oldCustomer === undefined) {
      this.getLoading().pipe(
        filter(loading => !loading),
        switchMap(() => this.matchingCustomers$),
        take(1),
        map(customers => ArrayUtil.first(customers))
      ).subscribe(customer => this.selectReferenceCustomer(customer));
    } else {
      this.selectReferenceCustomer(this.oldCustomer);
    }
  }

  private searchCustomer(type: CustomerType, name: string, registryKey: string): void {
    const searchQuery = registryKey.length >= REGISTRY_KEY_SEARCH_MIN_CHARS
      ? {name, registryKey}
      : {name};

    this.store.dispatch(new SearchByType(this.actionTargetType, {type, searchQuery, matchAny: true}));
  }
}
