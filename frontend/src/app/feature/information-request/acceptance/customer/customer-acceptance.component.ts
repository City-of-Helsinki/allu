import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromCustomerSearch from '@feature/customerregistry/reducers';
import {Customer} from '@model/customer/customer';
import {FormBuilder, FormGroup} from '@angular/forms';
import {CodeSetCodeMap} from '@model/codeset/codeset';
import {BehaviorSubject, Observable, Subject} from 'rxjs/index';
import {SetCustomer} from '../../actions/information-request-result-actions';
import {debounceTime, filter, map, switchMap, take, takeUntil} from 'rxjs/internal/operators';
import {SearchByType} from '@feature/customerregistry/actions/customer-search-actions';
import {ArrayUtil} from '@util/array-util';
import {CustomerType} from '@model/customer/customer-type';
import {CustomerNameSearchMinChars, REGISTRY_KEY_SEARCH_MIN_CHARS} from '@service/customer/customer-search-query';

export abstract class CustomerAcceptanceComponent implements OnInit, OnDestroy {

  @Input() oldCustomer: Customer;
  @Input() newCustomer: Customer;
  @Input() parentForm: FormGroup;
  @Input() readonly: boolean;

  referenceCustomer$: BehaviorSubject<Customer> = new BehaviorSubject<Customer>(undefined);
  referenceCustomerSelected: boolean;

  form: FormGroup;
  searchForm: FormGroup;

  countryCodes$: Observable<CodeSetCodeMap>;
  matchingCustomers$: Observable<Customer[]>;

  protected formName = 'customer';

  protected destroy = new Subject<boolean>();

  protected constructor(protected fb: FormBuilder,
              protected store: Store<fromRoot.State>) {}

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
    this.matchingCustomers$ = this.store.select(fromCustomerSearch.getMatchingCustomers);

    this.initialSearch();
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  abstract customerChanges(customer: Customer): void;

  selectReferenceCustomer(customer?: Customer): void {
    const searchCustomer = customer || this.newCustomer;
    const search = searchCustomer ? `${searchCustomer.name} (${searchCustomer.registryKey})` : undefined;
    this.searchForm.patchValue({search}, {emitEvent: false});
    this.referenceCustomer$.next(customer);
    this.referenceCustomerSelected = !!customer;
  }

  createNewCustomer(): void {
    this.selectReferenceCustomer();
  }

  private initialSearch() {
    this.searchCustomer(
      CustomerType[this.newCustomer.type],
      this.newCustomer.name,
      this.newCustomer.registryKey
    );

    if (this.oldCustomer === undefined) {
      this.store.select(fromCustomerSearch.getLoading).pipe(
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

    this.store.dispatch(new SearchByType({type, searchQuery, matchAny: true}));
  }
}
