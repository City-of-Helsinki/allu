import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';

import {CustomerForm} from '../../../customerregistry/customer/customer.form';
import {Some} from '../../../../util/option';
import {Subject} from 'rxjs/Subject';
import {NumberUtil} from '../../../../util/number.util';
import {MdDialog, MdDialogRef} from '@angular/material';
import {CustomerModalComponent} from '../../../customerregistry/customer/customer-modal.component';
import {Observable} from 'rxjs';
import {Customer} from '../../../../model/customer/customer';
import {CustomerWithContacts} from '../../../../model/customer/customer-with-contacts';
import {CustomerWithContactsForm} from '../../../customerregistry/customer/customer-with-contacts.form';
import {CustomerRoleType} from '../../../../model/customer/customer-role-type';

const ALWAYS_ENABLED_FIELDS = ['id', 'type', 'name', 'representative'];

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
  @Input() showCopyToBilling = false;
  @Input() showRepresentative = false;
  @Input() showPropertyDeveloper = false;
  @Input() propertyDeveloper = false;
  @Input() representative = false;
  @Input() addNew = false;

  customerWithContactsForm: FormGroup;
  customerForm: FormGroup;
  customerEvents$ = new Subject<Customer>();

  private dialogRef: MdDialogRef<CustomerModalComponent>;

  constructor(private fb: FormBuilder, private dialog: MdDialog) {
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
    this.parentForm.removeControl(CustomerWithContactsForm.formName(this.customerWithContacts.roleType));
  }

  canBeEdited(): boolean {
    return NumberUtil.isDefined(this.customerForm.value.id) && !this.readonly;
  }

  edit(): void {
    this.dialogRef = this.dialog.open(CustomerModalComponent, {disableClose: false, width: '800px'});
    this.dialogRef.componentInstance.customerId = this.customerForm.value.id;
    this.dialogRef.afterClosed()
      .filter(customer => !!customer)
      .subscribe(customer => this.customerForm.patchValue(CustomerForm.fromCustomer(customer)));
  }

  onCustomerChange(customer: Customer): void {
    if (NumberUtil.isDefined(customer.id)) {
      this.disableCustomerEdit();
    }
    this.customerEvents$.next(customer);
  }

  get customerEvents(): Observable<Customer> {
    return this.customerEvents$.asObservable();
  }

  private initForm() {
    let roleType = this.customerWithContacts.roleType;
    this.customerWithContactsForm = CustomerWithContactsForm.initialForm(this.fb, roleType);
    this.customerForm = <FormGroup>this.customerWithContactsForm.get('customer');
    this.customerForm.addControl('propertyDeveloper', this.fb.control(false));
    this.parentForm.addControl(CustomerWithContactsForm.formName(roleType), this.customerWithContactsForm);

    Some(this.customerWithContacts)
      .map(cwc => CustomerForm.fromCustomer(cwc.customer))
      .do(customer => {
        this.customerForm.patchValue(customer);
        this.disableCustomerEdit();
        this.customerEvents$.next(customer);
      });

    this.customerForm.patchValue({
      propertyDeveloper: this.propertyDeveloper,
      representative: this.representative
    });
  }

  private disableCustomerEdit(): void {
    Object.keys(this.customerForm.controls)
      .filter(key => ALWAYS_ENABLED_FIELDS.indexOf(key) < 0)
      .forEach(key => this.customerForm.get(key).disable());
  }
}
