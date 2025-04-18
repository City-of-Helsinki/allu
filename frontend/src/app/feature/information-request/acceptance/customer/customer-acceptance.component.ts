import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Customer} from '@model/customer/customer';
import {UntypedFormBuilder, UntypedFormControl, UntypedFormGroup} from '@angular/forms';
import {CodeSetCodeMap} from '@model/codeset/codeset';
import {BehaviorSubject, Observable, of, Subject} from 'rxjs/index';
import {debounceTime, filter, map, switchMap, take, takeUntil} from 'rxjs/internal/operators';
import {SearchByType} from '@feature/customerregistry/actions/customer-search-actions';
import {ArrayUtil} from '@util/array-util';
import {CustomerType} from '@model/customer/customer-type';
import {CustomerNameSearchMinChars, CustomerSearchQuery, REGISTRY_KEY_SEARCH_MIN_CHARS} from '@service/customer/customer-search-query';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {MatLegacyDialog as MatDialog} from '@angular/material/legacy-dialog';
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
import {findTranslation} from '@util/translations';
import {CONFIRM_DIALOG_MODAL_CONFIG, ConfirmDialogComponent} from '@feature/common/confirm-dialog/confirm-dialog.component';
import {FormGroup} from '@angular/forms';
import {FieldDescription, SelectFieldType} from '../field-select/field-description';
import {FieldSelectComponent} from '../field-select/field-select.component';
import { Some } from '@app/util/option';
import { UpdateCustomerReference } from '@feature/information-request/actions/information-request-result-actions';

@Component({
  selector: 'customer-acceptance',
  templateUrl: './customer-acceptance.component.html',
  styleUrls: ['./customer-acceptance.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerAcceptanceComponent implements OnInit, OnDestroy {

  @Input() oldCustomer: Customer;
  @Input() newCustomer: Customer;
  @Input() parentForm: UntypedFormGroup;
  @Input() readonly: boolean;
  @Input() fieldKey: InformationRequestFieldKey;
  @Input() canBeInvoiceRecipient = false;
  @Input() hideExisting = false;
  @Input() oldCustomerReference: string;
  @Input() newCustomerReference: string;
  @Input() showCustomerReference: boolean;
  @ViewChild('oldValuesSelect') oldValuesSelect: FieldSelectComponent;
  @ViewChild('newValuesSelect') newValuesSelect: FieldSelectComponent;

  @ViewChild(CustomerInfoAcceptanceComponent, { static: true }) infoAcceptance: CustomerInfoAcceptanceComponent;

  referenceCustomer$: BehaviorSubject<Customer> = new BehaviorSubject<Customer>(undefined);

  form: UntypedFormGroup;
  searchForm: UntypedFormGroup;

  countryCodes$: Observable<CodeSetCodeMap>;
  matchingCustomers$: Observable<Customer[]>;
  loading$: Observable<boolean>;

  protected formName = 'customer';
  protected actionTargetType = ActionTargetType.Customer;
  protected useForInvoicingCtrl: UntypedFormControl;
  protected roleType: CustomerRoleType;

  protected destroy = new Subject<boolean>();

  private config: CustomerAcceptanceConfig;

  selectionForm: FormGroup;
  referenceFieldDescriptions: FieldDescription[] = [
    new FieldDescription('customerReference', findTranslation('customer.customerReference'), SelectFieldType.TEXT)
  ];
  referenceFieldValues: any = {};
  referenceComparedValues: any = {};

  constructor(
    protected fb: UntypedFormBuilder,
    protected store: Store<fromRoot.State>,
    protected dialog: MatDialog) {
    this.selectionForm = this.fb.group({
      oldValues: [[]],
      newValues: [[]]
    });
  }

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
    ).subscribe(term => this.searchCustomer(this.newCustomer.type, term, term, term));

    this.initialSearch();
    this.init();

    this.referenceFieldValues = {
      customerReference: this.oldCustomerReference || ''
    };

    this.referenceComparedValues = {
      customerReference: this.newCustomerReference || ''
    };

    // Set initial selection of old value
    if (this.oldCustomerReference) {
      this.selectionForm.patchValue({
        oldValues: ['customerReference']
      });
    }

    // Handle selection changes
    this.selectionForm.get('oldValues').valueChanges.pipe(
      takeUntil(this.destroy)
    ).subscribe(oldValues => {
      if (oldValues && oldValues.length) {
        this.onOldValuesSelected(oldValues);
      }
    });

    this.selectionForm.get('newValues').valueChanges.pipe(
      takeUntil(this.destroy)
    ).subscribe(newValues => {
      if (newValues && newValues.length) {
        this.onNewValuesSelected(newValues);
      }
    });
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.oldCustomerReference || changes.newCustomerReference) {
      this.referenceFieldValues = {
        customerReference: this.oldCustomerReference || ''
      };
      this.referenceComparedValues = {
        customerReference: this.newCustomerReference || ''
      };
    }
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
    this.customerCreationConfirmation().pipe(
      filter(confirmed => confirmed),
      switchMap(() => this.dialog.open(CustomerModalComponent, config).afterClosed()),
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
      this.newCustomer.type,
      this.newCustomer.name,
      this.newCustomer.registryKey,
      this.newCustomer.sapCustomerNumber
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

  private searchCustomer(type: CustomerType, name: string, registryKey: string, sapCustomerNumber: string): void {
    const query: CustomerSearchQuery = {name, active: true, matchAny: true};

    if (registryKey && registryKey.length >= REGISTRY_KEY_SEARCH_MIN_CHARS) {
      query.registryKey = registryKey;
    }

    if (sapCustomerNumber) {
      query.sapCustomerNumber = sapCustomerNumber;
    }

    this.store.dispatch(new SearchByType(this.actionTargetType, {type, query}));
  }

  private customerCreationConfirmation(): Observable<boolean> {
    // We already might have a matching customer so ask confirmation from
    // user if they really want to create user since it is usually a mistake
    if (NumberUtil.isExisting(this.referenceCustomer$.getValue())) {
      const data = {
        title: findTranslation(['customer.confirmCreate.title']),
        description: findTranslation(['customer.confirmCreate.description']),
        confirmText: findTranslation(['customer.confirmCreate.confirmText']),
        cancelText: findTranslation(['customer.confirmCreate.cancelText'])
      };
      const config = {
        ...CONFIRM_DIALOG_MODAL_CONFIG,
        data
      };
      return this.dialog.open(ConfirmDialogComponent, config).afterClosed();
    } else {
      return of(true);
    }
  }

  onOldValuesSelected(fields: string[]): void {
    if (fields.includes('customerReference')) {
      this.updateApplicationReference(this.oldCustomerReference);
      Some(this.newValuesSelect).do(select => select.deselect('customerReference'));
    }
    this.form.updateValueAndValidity();
  }

  onNewValuesSelected(fields: string[]): void {
    if (fields.includes('customerReference')) {
      this.updateApplicationReference(this.newCustomerReference);
      Some(this.oldValuesSelect).do(select => select.deselect('customerReference'));
    }
    this.form.updateValueAndValidity();
  }

  private updateApplicationReference(reference: string): void {
    this.store.dispatch(new UpdateCustomerReference(reference));
  }
}
