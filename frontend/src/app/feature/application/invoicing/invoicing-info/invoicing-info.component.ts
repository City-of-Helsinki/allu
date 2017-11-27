import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {CustomerType} from '../../../../model/customer/customer-type';
import {EnumUtil} from '../../../../util/enum.util';
import {InvoicePartition} from '../../../../model/application/invoice/ivoice-partition';
import {ApplicationState} from '../../../../service/application/application-state';
import {CustomerHub} from '../../../../service/customer/customer-hub';
import {Some} from '../../../../util/option';
import {CustomerForm} from '../../../customerregistry/customer/customer.form';
import {Application} from '../../../../model/application/application';
import {ALWAYS_ENABLED_FIELDS} from '../../../customerregistry/customer/customer-info.component';
import {
  CUSTOMER_MODAL_CONFIG,
  CustomerModalComponent
} from '../../../customerregistry/customer/customer-modal.component';
import {MatDialog, MatDialogRef} from '@angular/material';
import {NumberUtil} from '../../../../util/number.util';

@Component({
  selector: 'invoicing-info',
  templateUrl: './invoicing-info.component.html',
  styleUrls: [
    './invoicing-info.component.scss'
  ]
})
export class InvoicingInfoComponent implements OnInit {

  @Input() form: FormGroup;

  customerTypes = EnumUtil.enumValues(CustomerType);
  invoicePartitions = EnumUtil.enumValues(InvoicePartition);
  recipientForm: FormGroup;

  private notBillableCtrl: FormControl;
  private notBillableReasonCtrl: FormControl;
  private dialogRef: MatDialogRef<CustomerModalComponent>;

  constructor(private applicationState: ApplicationState,
              private customerHub: CustomerHub,
              private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.recipientForm = <FormGroup>this.form.get('invoiceRecipient');
    this.notBillableCtrl = <FormControl>this.form.get('notBillable');
    this.notBillableReasonCtrl = <FormControl>this.form.get('notBillableReason');

    this.patchInfo(this.applicationState.application);
    Some(this.applicationState.application.invoiceRecipientId).do(id => this.findAndPatchCustomer(id));
    this.notBillableCtrl.valueChanges.subscribe(value => this.onNotBillableChange(value));
  }



  invoiceRecipientChange(recipient: CustomerForm) {
    if (recipient.id) {
      this.disableCustomerEdit();
    }
  }

  editCustomer(): void {
    this.dialogRef = this.dialog.open<CustomerModalComponent>(CustomerModalComponent, CUSTOMER_MODAL_CONFIG);
    this.dialogRef.componentInstance.customerId = this.recipientForm.value.id;
    this.dialogRef.afterClosed()
      .filter(customer => !!customer)
      .subscribe(customer => this.recipientForm.patchValue(CustomerForm.fromCustomer(customer)));
  }

  get billable(): boolean {
    return !this.notBillableCtrl.value;
  }

  canBeEdited(): boolean {
    return NumberUtil.isDefined(this.recipientForm.value.id) && this.billable;
  }

  get invoicingProhibited(): boolean {
    return this.recipientForm.getRawValue().invoicingProhibited && this.billable;
  }

  private patchInfo(application: Application): void {
    this.form.patchValue({
      notBillable: application.notBillable,
      notBillableReason: application.notBillableReason
    });
  }

  private findAndPatchCustomer(id: number): void {
    this.disableCustomerEdit();
    this.customerHub.findCustomerById(id)
      .subscribe(customer => this.recipientForm.patchValue(CustomerForm.fromCustomer(customer)));
  }

  private onNotBillableChange(notBillable: boolean) {
    if (notBillable) {
      this.notBillableReasonCtrl.setValidators([Validators.required]);
      this.form.removeControl('invoiceRecipient');
    } else {
      this.notBillableReasonCtrl.clearValidators();
      this.form.addControl('invoiceRecipient', this.recipientForm);
    }
  }

  private disableCustomerEdit(): void {
    Object.keys(this.recipientForm.controls)
      .filter(key => ALWAYS_ENABLED_FIELDS.indexOf(key) < 0)
      .forEach(key => this.recipientForm.get(key).disable({emitEvent: false}));
  }
}
