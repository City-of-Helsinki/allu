import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
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
import {CustomerRoleType} from '../../../../model/customer/customer-role-type';
import {ApplicationStore} from '../../../../service/application/application-store';
import {Subscription} from 'rxjs/Subscription';
import {Application} from '../../../../model/application/application';

@Component({
  selector: 'customer',
  viewProviders: [],
  templateUrl: './customer.component.html',
  styleUrls: [
    './customer.component.scss'
  ]
})
export class CustomerComponent implements OnInit, OnDestroy {
  @Input() parentForm: FormGroup;
  @Input() roleType = CustomerRoleType[CustomerRoleType.APPLICANT];
  @Input() readonly: boolean;
  @Input() showRepresentative = false;
  @Input() showPropertyDeveloper = false;
  @Input() contactRequired = false;

  @ViewChild('contacts') contacts: ContactComponent;

  customerWithContactsForm: FormGroup;
  customerForm: FormGroup;

  private dialogRef: MatDialogRef<CustomerModalComponent>;
  private appSubscription: Subscription;

  constructor(private fb: FormBuilder, private dialog: MatDialog, private applicationStore: ApplicationStore) {
  }

  ngOnInit(): void {
    if (!this.roleType) {
      throw Error('Missing input for roleType');
    }

    this.initForm();

    if (this.readonly) {
      this.customerForm.disable();
    }

    this.appSubscription = this.applicationStore.application.subscribe(app => this.onApplicationChange(app));
  }

  ngOnDestroy(): void {
    this.contacts.onCustomerRemove();
    this.parentForm.removeControl(CustomerWithContactsForm.formName(CustomerRoleType[this.roleType]));
    this.appSubscription.unsubscribe();
  }

  canBeEdited(): boolean {
    return !this.readonly && Some(this.customerForm.value).map(val => val.id).orElse(false);
  }

  edit(): void {
    this.dialogRef = this.dialog.open<CustomerModalComponent>(CustomerModalComponent, CUSTOMER_MODAL_CONFIG);
    this.dialogRef.componentInstance.customerId = this.customerForm.value.id;
    this.dialogRef.afterClosed()
      .filter(customer => !!customer)
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
    const roleType = CustomerRoleType[this.roleType];
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

  private onApplicationChange(application: Application) {
    Some(application.customerWithContactsByRole(CustomerRoleType[this.roleType]))
      .map(cwc => cwc.customer)
      .filter(customer => !!customer)
      .map(customer => CustomerForm.fromCustomer(customer))
      .do(customer => {
        this.customerForm.patchValue(customer);
        this.disableEdit(customer);
      });

  }
}
