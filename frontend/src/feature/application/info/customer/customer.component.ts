import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';

import {CustomerForm} from '../../../customerregistry/customer/customer.form';
import {Some} from '../../../../util/option';
import {NumberUtil} from '../../../../util/number.util';
import {MatDialog, MatDialogRef} from '@angular/material';
import {CustomerModalComponent} from '../../../customerregistry/customer/customer-modal.component';
import {Customer} from '../../../../model/customer/customer';
import {CustomerWithContacts} from '../../../../model/customer/customer-with-contacts';
import {CustomerWithContactsForm} from '../../../customerregistry/customer/customer-with-contacts.form';
import {ContactComponent} from '../contact/contact.component';
import {ALWAYS_ENABLED_FIELDS} from '../../../customerregistry/customer/customer-info.component';

@Component({
  selector: 'customer',
  viewProviders: [],
  template: require('./customer.component.html'),
  styles: [
    require('./customer.component.scss')
  ]
})
export class CustomerComponent implements OnInit, OnDestroy {
  @Input() parentForm: FormGroup;
  @Input() customerWithContacts: CustomerWithContacts;
  @Input() readonly: boolean;
  @Input() showRepresentative = false;
  @Input() showPropertyDeveloper = false;
  @Input() contactRequired = false;

  @ViewChild('contacts') contacts: ContactComponent;

  customerWithContactsForm: FormGroup;
  customerForm: FormGroup;

  private dialogRef: MatDialogRef<CustomerModalComponent>;

  constructor(private fb: FormBuilder, private dialog: MatDialog) {
  }

  ngOnInit(): void {
    if (!this.customerWithContacts) {
      throw Error('Missing input for customerWithContacts');
    }

    this.initForm();

    if (this.readonly) {
      this.customerForm.disable();
    }
  }

  ngOnDestroy(): void {
    this.contacts.onCustomerRemove();
    this.parentForm.removeControl(CustomerWithContactsForm.formName(this.customerWithContacts.roleType));
  }

  canBeEdited(): boolean {
    return NumberUtil.isDefined(this.customerForm.value.id) && !this.readonly;
  }

  edit(): void {
    this.dialogRef = this.dialog.open<CustomerModalComponent>(CustomerModalComponent, {
      disableClose: false,
      width: '800px'
    });
    this.dialogRef.componentInstance.customerId = this.customerForm.value.id;
    this.dialogRef.afterClosed()
      .filter(customer => !!customer)
      .subscribe(customer => this.customerForm.patchValue(CustomerForm.fromCustomer(customer)));
  }

  onCustomerChange(customer: Customer): void {
    if (NumberUtil.isDefined(customer.id)) {
      this.disableCustomerEdit();
    }

    this.contacts.onCustomerChange(customer.id);
  }

  onRepresentativeChange(checked: boolean): void {
    this.parentForm.patchValue({hasRepresentative: checked});
  }

  onPropertyDeveloperChange(checked: boolean): void {
    this.parentForm.patchValue({hasPropertyDeveloper: checked});
  }

  private initForm() {
    let roleType = this.customerWithContacts.roleType;
    this.customerWithContactsForm = CustomerWithContactsForm.initialForm(this.fb, roleType);
    this.customerForm = <FormGroup>this.customerWithContactsForm.get('customer');
    this.parentForm.addControl(CustomerWithContactsForm.formName(roleType), this.customerWithContactsForm);

    Some(this.customerWithContacts)
      .filter(cwc => NumberUtil.isDefined(cwc.customerId))
      .map(cwc => CustomerForm.fromCustomer(cwc.customer))
      .do(customer => {
        this.customerForm.patchValue(customer);
        this.disableCustomerEdit();
      });
  }

  private disableCustomerEdit(): void {
    Object.keys(this.customerForm.controls)
      .filter(key => ALWAYS_ENABLED_FIELDS.indexOf(key) < 0)
      .forEach(key => this.customerForm.get(key).disable());
  }
}
