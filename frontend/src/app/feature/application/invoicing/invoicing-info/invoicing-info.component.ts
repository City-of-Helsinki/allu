import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {CustomerType} from '../../../../model/customer/customer-type';
import {EnumUtil} from '../../../../util/enum.util';
import {InvoicePartition} from '../../../../model/application/invoice/ivoice-partition';
import {ApplicationStore} from '../../../../service/application/application-store';
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
import {DEPOSIT_MODAL_CONFIG, DepositModalComponent} from '../deposit/deposit-modal.component';
import {NotificationService} from '../../../../service/notification/notification.service';
import {Deposit} from '../../../../model/application/invoice/deposit';
import {ObjectUtil} from '../../../../util/object.util';
import {DepositStatusType} from '../../../../model/application/invoice/deposit-status-type';
import {applicationCanBeEdited} from '../../../../model/application/application-status';

@Component({
  selector: 'invoicing-info',
  templateUrl: './invoicing-info.component.html',
  styleUrls: [
    './invoicing-info.component.scss'
  ]
})
export class InvoicingInfoComponent implements OnInit {

  @Input() form: FormGroup;

  recipientForm: FormGroup;

  private notBillableCtrl: FormControl;
  private notBillableReasonCtrl: FormControl;

  constructor(private applicationStore: ApplicationStore,
              private customerHub: CustomerHub,
              private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.recipientForm = <FormGroup>this.form.get('invoiceRecipient');
    this.notBillableCtrl = <FormControl>this.form.get('notBillable');
    this.notBillableReasonCtrl = <FormControl>this.form.get('notBillableReason');
    this.initForm();
    this.notBillableCtrl.valueChanges.subscribe(value => this.onNotBillableChange(value));
  }

  invoiceRecipientChange(recipient: CustomerForm) {
    if (recipient.id) {
      this.disableCustomerEdit();
    }
  }

  editCustomer(): void {
    const customerModalRef = this.dialog.open<CustomerModalComponent>(CustomerModalComponent, CUSTOMER_MODAL_CONFIG);
    customerModalRef.componentInstance.customerId = this.recipientForm.value.id;
    customerModalRef.afterClosed()
      .filter(customer => !!customer)
      .subscribe(customer => this.recipientForm.patchValue(CustomerForm.fromCustomer(customer)));
  }

  editDeposit(): void {
    const deposit = this.currentDeposit();
    const config = {...DEPOSIT_MODAL_CONFIG, data: {deposit: deposit}};

    const depositModalRef = this.dialog.open<DepositModalComponent>(DepositModalComponent, config);
    depositModalRef.afterClosed()
      .filter(result => !!result)
      .switchMap(result => this.applicationStore.saveDeposit(result))
      .subscribe(
        result => NotificationService.translateMessage('deposit.action.save'),
        error => NotificationService.error(error));
  }

  nextDepositStatus(): void {
    const deposit = this.currentDeposit();
    deposit.status = deposit.status + 1;
    this.applicationStore.saveDeposit(deposit)
      .subscribe(
        result => NotificationService.translateMessage('deposit.action.save'),
        error => NotificationService.error(error));
  }

  get hasDeposit(): boolean {
    return !!this.form.getRawValue().depositStatus;
  }

  get canChangeDepositStatus(): boolean {
    const deposit = this.applicationStore.snapshot.deposit;
    return deposit ? deposit.status < DepositStatusType.RETURNED_DEPOSIT : false;
  }

  get billable(): boolean {
    return !this.notBillableCtrl.value;
  }

  customerCanBeEdited(): boolean {
    return NumberUtil.isDefined(this.recipientForm.value.id) && this.billable;
  }

  get invoicingProhibited(): boolean {
    return this.recipientForm.getRawValue().invoicingProhibited && this.billable;
  }

  private initForm(): void {
    this.applicationStore.application.subscribe(app => {
      Some(app.invoiceRecipientId).do(id => this.findAndPatchCustomer(id));

      this.form.patchValue({
        notBillable: app.notBillable,
        notBillableReason: app.notBillableReason,
        customerReference: app.customerReference
      });

      if (!applicationCanBeEdited(app.statusEnum)) {
        this.form.disable();
      }
    });

    this.applicationStore.deposit
      .filter(deposit => !!deposit)
      .subscribe(deposit => this.form.patchValue({
          depositAmount: deposit.amountEuro,
          depositReason: deposit.reason,
          depositStatus: deposit.uiStatus
        })
      );

    this.applicationStore.loadDeposit().subscribe();
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

  private currentDeposit(): Deposit {
    const applicationId = this.applicationStore.snapshot.application.id;
    return this.applicationStore.snapshot.deposit || Deposit.forApplication(applicationId);
  }
}
