import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromCustomerSearch from '@feature/customerregistry/reducers';
import {Customer} from '@model/customer/customer';
import {FormBuilder, FormGroup} from '@angular/forms';
import {CodeSetCodeMap} from '@model/codeset/codeset';
import {BehaviorSubject, Observable, Subject} from 'rxjs/index';
import {SetInvoicingCustomer} from '../../actions/information-request-result-actions';
import {debounceTime, filter, map, switchMap, take, takeUntil} from 'rxjs/internal/operators';
import {SearchByType} from '@feature/customerregistry/actions/customer-search-actions';
import {ArrayUtil} from '@util/array-util';
import {CustomerNameSearchMinChars, REGISTRY_KEY_SEARCH_MIN_CHARS} from '@service/customer/customer-search-query';
import {CustomerType} from '@model/customer/customer-type';

@Component({
  selector: 'invoice-customer-acceptance',
  templateUrl: './invoice-customer-acceptance.component.html',
  styleUrls: []
})
export class InvoiceCustomerAcceptanceComponent implements OnInit, OnDestroy {

  @Input() oldCustomer: Customer;
  @Input() newCustomer: Customer;
  @Input() parentForm: FormGroup;
  @Input() readonly: boolean;

  referenceCustomer$: BehaviorSubject<Customer>;

  form: FormGroup;
  searchForm: FormGroup;

  countryCodes$: Observable<CodeSetCodeMap>;
  matchingCustomers$: Observable<Customer[]>;

  private destroy = new Subject<boolean>();

  constructor(private fb: FormBuilder,
              private store: Store<fromRoot.State>) {}

  ngOnInit(): void {
    this.form = this.fb.group({});
    this.parentForm.addControl('invoice-customer', this.form);
    this.searchForm = this.fb.group({
      search: undefined
    });
    this.countryCodes$ = this.store.select(fromRoot.getCodeSetCodeMap('Country'));
    this.referenceCustomer$ = new BehaviorSubject<Customer>(this.oldCustomer);

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

  customerChanges(customer: Customer): void {
    this.store.dispatch(new SetInvoicingCustomer(customer));
  }

  selectReferenceCustomer(customer?: Customer): void {
    const search = customer ? `${customer.name} (${customer.registryKey})` : undefined;
    this.searchForm.patchValue({search}, {emitEvent: false});
    this.referenceCustomer$.next(customer);
  }

  createNewCustomer(): void {
    this.searchForm.reset();
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
      ).subscribe(customer => {
        this.selectReferenceCustomer(customer);
      });
    }
  }

  private searchCustomer(type: CustomerType, name: string, registryKey: string): void {
    const searchQuery = registryKey.length >= REGISTRY_KEY_SEARCH_MIN_CHARS
      ? {name, registryKey}
      : {name};

    this.store.dispatch(new SearchByType({type, searchQuery, matchAny: true}));
  }
}
