import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {CustomerForm} from '@feature/customerregistry/customer/customer.form';
import {Some} from '@util/option';
import {NumberUtil} from '@util/number.util';
import {Customer} from '@model/customer/customer';
import {CustomerWithContactsForm} from '@feature/customerregistry/customer/customer-with-contacts.form';
import {ContactComponent} from '../contact/contact.component';
import {ALWAYS_ENABLED_FIELDS} from '@feature/customerregistry/customer/customer-info.component';
import {CustomerWithContacts} from '@model/customer/customer-with-contacts';
import {CustomerType} from '@model/customer/customer-type';
import {InformationRequestModalEvents} from '@feature/information-request/information-request-modal-events';
import {CustomerService} from '@service/customer/customer.service';
import {findTranslation} from '@util/translations';
import {NotificationService} from '@feature/notification/notification.service';
import {Contact} from '@model/customer/contact';

@Component({
  selector: 'customer',
  viewProviders: [],
  templateUrl: './customer.component.html',
  styleUrls: [
    './customer.component.scss'
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CustomerComponent implements OnInit, OnDestroy {
  @Input() pendingInfo: boolean;
  @Input() parentForm: FormGroup;
  @Input() readonly: boolean;
  @Input() showRepresentative = false;
  @Input() showPropertyDeveloper = false;
  @Input() contactRequired = false;

  @Output() showPendingInfo = new EventEmitter<{}>();

  @ViewChild('contacts') contacts: ContactComponent;

  customerWithContactsForm: FormGroup;
  customerForm: FormGroup;
  customerClass: string[] = [];

  private _customerWithContacts: CustomerWithContacts;

  constructor(private fb: FormBuilder,
              private modalState: InformationRequestModalEvents,
              private customerService: CustomerService,
              private notification: NotificationService) {
  }

  @Input() set customerWithContacts(customerWithContacts) {
    this._customerWithContacts = customerWithContacts;
    this.onCustomerWithContactsChange();
  }

  get customerWithContacts() {
    return this._customerWithContacts;
  }

  ngOnInit(): void {
    if (!this.customerWithContacts) {
      throw Error('Missing input for customer');
    }

    this.initForm();

    if (this.readonly) {
      this.customerForm.disable();
    }

    this.onCustomerWithContactsChange();
    this.customerForm.get('type').valueChanges
      .subscribe(type => this.onCustomerTypeChange(type));
  }

  ngOnDestroy(): void {
    this.contacts.onCustomerRemove();
    this.parentForm.removeControl(CustomerWithContactsForm.formName(this.customerWithContacts.roleType));
  }

  showPending(): void {
    this.modalState.openAcceptance();
  }

  onCustomerChange(customer: Customer): void {
    this.disableEdit(customer);
    this.contacts.onCustomerChange(customer.id);
  }

  contactSelected(contact: Contact): void {
    // When contact is selected and no existing customer is selected
    // Customer should be filled based on contacts customer
    if (this.isNewCustomer) {
      this.customerService.findCustomerById(contact.customerId).subscribe(customer => {
        this.customerForm.patchValue(CustomerForm.fromCustomer(customer));
      });
    }
  }

  onRepresentativeChange(checked: boolean): void {
    this.parentForm.patchValue({hasRepresentative: checked});
  }

  onPropertyDeveloperChange(checked: boolean): void {
    this.parentForm.patchValue({hasPropertyDeveloper: checked});
  }

  get isNewCustomer() {
    return !NumberUtil.isDefined(this.customerForm.getRawValue().id);
  }

  save(form: CustomerForm): void {
    this.customerService.saveCustomer(CustomerForm.toCustomer(form)).subscribe(
      saved => {
        this.notification.success(findTranslation('customer.action.save'));
        this.customerForm.patchValue(CustomerForm.fromCustomer(saved), {emitEvent: false});
        this.onCustomerChange(saved);
      }, error => this.notification.errorInfo(error));
  }

  private initForm() {
    const roleType = this.customerWithContacts.roleType;
    this.customerWithContactsForm = CustomerWithContactsForm.initialForm(this.fb, roleType);
    this.customerForm = <FormGroup>this.customerWithContactsForm.get('customer');
    this.parentForm.addControl(CustomerWithContactsForm.formName(roleType), this.customerWithContactsForm);
  }

  private disableEdit(customer: Customer): void {
    if (NumberUtil.isDefined(customer.id)) {
      Object.keys(this.customerForm.controls)
        .filter(key => ALWAYS_ENABLED_FIELDS.indexOf(key) < 0)
        .forEach(key => this.customerForm.get(key).disable());
    }
  }

  private onCustomerWithContactsChange() {
    if (this.customerForm) {
      Some(this.customerWithContacts)
        .map(cwc => cwc.customer)
        .filter(customer => !!customer)
        .do(customer => this.disableEdit(customer))
        .map(customer => CustomerForm.fromCustomer(customer))
        .do(customer => {
          this.customerForm.patchValue(customer);
          this.onCustomerTypeChange(customer.type);
        });
    }
  }

  private onCustomerTypeChange(type: string) {
    const classes = [];
    if (CustomerType[type] === CustomerType.PERSON) {
      classes.push('customer-person');
    }

    if (this.showPropertyDeveloper || this.showRepresentative) {
      classes.push('customer-additional-toggle');
    }

    this.customerClass = classes;
  }
}
