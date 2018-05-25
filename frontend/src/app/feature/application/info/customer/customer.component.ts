import {ChangeDetectionStrategy, Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {CustomerForm} from '../../../customerregistry/customer/customer.form';
import {Some} from '../../../../util/option';
import {NumberUtil} from '../../../../util/number.util';
import {MatDialog, MatDialogRef} from '@angular/material';
import {
  CUSTOMER_MODAL_CONFIG,
  CustomerModalComponent
} from '../../../customerregistry/customer/customer-modal.component';
import {Customer} from '../../../../model/customer/customer';
import {CustomerWithContactsForm} from '../../../customerregistry/customer/customer-with-contacts.form';
import {ContactComponent} from '../contact/contact.component';
import {ALWAYS_ENABLED_FIELDS} from '../../../customerregistry/customer/customer-info.component';
import {CustomerWithContacts} from '../../../../model/customer/customer-with-contacts';
import {filter} from 'rxjs/internal/operators';
import {CustomerType} from '../../../../model/customer/customer-type';

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
  @Input() customerWithContacts: CustomerWithContacts;
  @Input() parentForm: FormGroup;
  @Input() readonly: boolean;
  @Input() showRepresentative = false;
  @Input() showPropertyDeveloper = false;
  @Input() contactRequired = false;

  @ViewChild('contacts') contacts: ContactComponent;

  customerWithContactsForm: FormGroup;
  customerForm: FormGroup;
  customerClass: string[] = [];

  private dialogRef: MatDialogRef<CustomerModalComponent>;

  constructor(private fb: FormBuilder, private dialog: MatDialog) {
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

  canBeEdited(): boolean {
    return Some(this.customerForm.value).map(val => val.id).orElse(false);
  }

  edit(): void {
    this.dialogRef = this.dialog.open<CustomerModalComponent>(CustomerModalComponent, CUSTOMER_MODAL_CONFIG);
    this.dialogRef.componentInstance.customerId = this.customerForm.value.id;
    this.dialogRef.afterClosed().pipe(filter(customer => !!customer))
      .subscribe(customer => this.customerForm.patchValue(CustomerForm.fromCustomer(customer)));
  }

  onCustomerChange(customer: Customer): void {
    this.disableEdit(customer);
    this.contacts.onCustomerChange(customer.id);
  }

  onRepresentativeChange(checked: boolean): void {
    this.parentForm.patchValue({hasRepresentative: checked});
  }

  onPropertyDeveloperChange(checked: boolean): void {
    this.parentForm.patchValue({hasPropertyDeveloper: checked});
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
    Some(this.customerWithContacts)
      .map(cwc => cwc.customer)
      .filter(customer => !!customer)
      .map(customer => CustomerForm.fromCustomer(customer))
      .do(customer => {
        this.customerForm.patchValue(customer);
        this.disableEdit(customer);
        this.onCustomerTypeChange(customer.type);
      });

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
