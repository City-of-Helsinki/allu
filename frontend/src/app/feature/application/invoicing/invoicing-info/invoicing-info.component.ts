import {Component, Input, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {ApplicationStore} from '../../../../service/application/application-store';
import {Some} from '../../../../util/option';
import {CustomerForm} from '../../../customerregistry/customer/customer.form';
import {Application} from '../../../../model/application/application';
import {ALWAYS_ENABLED_FIELDS} from '../../../customerregistry/customer/customer-info.component';
import {
  CUSTOMER_MODAL_CONFIG,
  CustomerModalComponent
} from '../../../customerregistry/customer/customer-modal.component';
import {MatDialog} from '@angular/material';
import {NumberUtil} from '../../../../util/number.util';
import {DEPOSIT_MODAL_CONFIG, DepositModalComponent} from '../deposit/deposit-modal.component';
import {NotificationService} from '../../../../service/notification/notification.service';
import {Deposit} from '../../../../model/application/invoice/deposit';
import {DepositStatusType} from '../../../../model/application/invoice/deposit-status-type';
import {applicationCanBeEdited} from '../../../../model/application/application-status';
import {InvoicingInfoForm} from './invoicing-info.form';
import {CustomerService} from '../../../../service/customer/customer.service';

@Component({
  selector: 'invoicing-info',
  templateUrl: './invoicing-info.component.html',
  styleUrls: [
    './invoicing-info.component.scss'
  ]
})
export class InvoicingInfoComponent implements OnInit {

  @Input() form: FormGroup;
  @Input() reset: Observable<boolean>;

  recipientForm: FormGroup;

  private notBillableCtrl: FormControl;
  private notBillableReasonCtrl: FormControl;
  private invoicingDateCtrl: FormControl;
  private originalForm: InvoicingInfoForm;
  private originalRecipientForm: CustomerForm;

  constructor(private applicationStore: ApplicationStore,
              private customerService: CustomerService,
              private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.recipientForm = <FormGroup>this.form.get('invoiceRecipient');
    this.notBillableCtrl = <FormControl>this.form.get('notBillable');
    this.notBillableReasonCtrl = <FormControl>this.form.get('notBillableReason');
    this.invoicingDateCtrl = <FormControl>this.form.get('invoicingDate');
    this.notBillableCtrl.valueChanges.subscribe(value => this.onNotBillableChange(value));
    this.initForm();
    this.reset.filter(r => r).subscribe(r => this.resetMe(r));
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
        customerReference: app.customerReference,
        invoicingDate: app.invoicingDate ? app.invoicingDate : this.defaultInvoicingDate(app),
        skipPriceCalculation: app.skipPriceCalculation
      });
      this.originalForm = this.form.getRawValue();

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
    this.customerService.findCustomerById(id)
      .subscribe(customer => {
        this.recipientForm.patchValue(CustomerForm.fromCustomer(customer));
        this.originalRecipientForm = this.recipientForm.getRawValue();
    });
  }

  private onNotBillableChange(notBillable: boolean) {
    if (notBillable) {
      this.invoicingDateCtrl.clearValidators();
      this.notBillableReasonCtrl.setValidators([Validators.required]);
      this.form.removeControl('invoiceRecipient');
    } else {
      this.invoicingDateCtrl.setValidators([Validators.required]);
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

  private defaultInvoicingDate(application: Application): Date {
    const currentDate = new Date();
    const result = new Date(application.startTime);
    result.setDate(result.getDate() - 15);
    return result > currentDate ? result : currentDate;
  }

  private resetMe(reset: boolean) {
    if (this.originalForm) {
      this.form.reset(this.originalForm);
    } else {
      this.form.reset();
    }
    if (this.originalRecipientForm) {
      this.recipientForm.reset(this.originalRecipientForm);
    } else {
      this.recipientForm.reset();
    }
  }
}
