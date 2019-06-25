import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Customer} from '@model/customer/customer';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
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
import {InformationRequestFieldKey} from '@model/information-request/information-request-field-key';
import {NumberUtil} from '@util/number.util';
import {LoadByCustomer, LoadByCustomerSuccess} from '@feature/customerregistry/actions/contact-search-actions';
import {CustomerRoleType} from '@model/customer/customer-role-type';
import {UseCustomerForInvoicing} from '@feature/information-request/actions/information-request-result-actions';
import {
  config as acceptanceConfig,
  CustomerAcceptanceConfig
} from '@feature/information-request/acceptance/customer/customer-acceptance-config';

@Component({
  selector: 'customer-acceptance',
  templateUrl: './customer-acceptance.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerAcceptanceComponent implements OnInit, OnDestroy {

  @Input() oldCustomer: Customer;
  @Input() newCustomer: Customer;
  @Input() parentForm: FormGroup;
  @Input() readonly: boolean;
  @Input() fieldKey: InformationRequestFieldKey;
  @Input() canBeInvoiceRecipient = false;

  @ViewChild(CustomerInfoAcceptanceComponent) infoAcceptance: CustomerInfoAcceptanceComponent;

  referenceCustomer$: BehaviorSubject<Customer> = new BehaviorSubject<Customer>(undefined);

  form: FormGroup;
  searchForm: FormGroup;

  countryCodes$: Observable<CodeSetCodeMap>;
  matchingCustomers$: Observable<Customer[]>;
  loading$: Observable<boolean>;

  protected formName = 'customer';
  protected actionTargetType = ActionTargetType.Customer;
  protected useForInvoicingCtrl: FormControl;
  protected roleType: CustomerRoleType;

  protected destroy = new Subject<boolean>();

  private config: CustomerAcceptanceConfig;

  constructor(
    protected fb: FormBuilder,
    protected store: Store<fromRoot.State>,
    protected dialog: MatDialog) {}

  ngOnInit(): void {
    this.config = acceptanceConfig[this.fieldKey];
    this.formName = this.config.formName;
    this.roleType = this.config.roleType;
    this.actionTargetType = this.config.actionTargetType;
    this.matchingCustomers$ = this.store.pipe(select(this.config.matchingCustomersSelector));
    this.loading$ = this.store.pipe(select(this.config.customersLoadingSelector));

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

    this.initialSearch();
    this.init();
  }

  init(): void {
    if (this.canBeInvoiceRecipient) {
      this.useForInvoicingCtrl = this.fb.control(false);
      this.searchForm.addControl('useForInvoicing', this.useForInvoicingCtrl);

      this.useForInvoicingCtrl.valueChanges.pipe(
        takeUntil(this.destroy),
        map(useCustomerForInvoicing => useCustomerForInvoicing ? this.roleType : undefined)
      ).subscribe(roleType => {
        this.store.dispatch(new UseCustomerForInvoicing(roleType));
      });
    }
  }

  ngOnDestroy(): void {
    this.parentForm.removeControl(this.formName);
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  customerChanges(customer: Customer): void {
    if (this.config.saveAction) {
      this.store.dispatch(this.config.saveAction(customer));
    }
  }

  selectReferenceCustomer(customer?: Customer): void {
    const search = customer ? `${customer.name} (${customer.registryKey})` : undefined;
    this.searchForm.patchValue({search}, {emitEvent: false});
    this.referenceCustomer$.next(customer);
    this.onSelectReferenceCustomer(customer);
  }

  protected onSelectReferenceCustomer(customer?: Customer): void {
    if (NumberUtil.isExisting(customer)) {
      this.store.dispatch(new LoadByCustomer(this.actionTargetType, customer.id));
    } else {
      this.store.dispatch(new LoadByCustomerSuccess(this.actionTargetType, []));
    }
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
      this.loading$.pipe(
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
    const searchQuery = registryKey && registryKey.length >= REGISTRY_KEY_SEARCH_MIN_CHARS
      ? {name, registryKey, active: true}
      : {name, active: true};

    this.store.dispatch(new SearchByType(this.actionTargetType, {type, searchQuery, matchAny: true}));
  }
}
