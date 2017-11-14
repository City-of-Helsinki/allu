import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {CustomerType} from '../../../../model/customer/customer-type';
import {EnumUtil} from '../../../../util/enum.util';
import {InvoicingInfoForm} from './invoicing-info.form';
import {InvoicePartition} from '../../../../model/application/invoice/ivoice-partition';
import {ApplicationState} from '../../../../service/application/application-state';
import {CustomerHub} from '../../../../service/customer/customer-hub';
import {Some} from '../../../../util/option';
import {CustomerForm} from '../../../customerregistry/customer/customer.form';
import {Application} from '../../../../model/application/application';
import {ALWAYS_ENABLED_FIELDS} from '../../../customerregistry/customer/customer-info.component';
import {NotificationService} from '../../../../service/notification/notification.service';
import {findTranslation} from '../../../../util/translations';
import {Observable} from 'rxjs/Observable';
import {Customer} from '../../../../model/customer/customer';
import {CUSTOMER_MODAL_CONFIG, CustomerModalComponent} from '../../../customerregistry/customer/customer-modal.component';
import {MatDialog, MatDialogRef} from '@angular/material';
import {NumberUtil} from '../../../../util/number.util';

@Component({
  selector: 'invoicing-info',
  template: require('./invoicing-info.component.html'),
  styles: [
    require('./invoicing-info.component.scss')
  ]
})
export class InvoicingInfoComponent implements OnInit {

  customerTypes = EnumUtil.enumValues(CustomerType);
  invoicePartitions = EnumUtil.enumValues(InvoicePartition);
  infoForm: FormGroup;
  recipientForm: FormGroup;

  private notBillableCtrl: FormControl;
  private notBillableReasonCtrl: FormControl;
  private dialogRef: MatDialogRef<CustomerModalComponent>;

  constructor(private fb: FormBuilder,
              private applicationState: ApplicationState,
              private customerHub: CustomerHub,
              private dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.infoForm = InvoicingInfoForm.initialForm(this.fb);
    this.recipientForm = <FormGroup>this.infoForm.get('invoiceRecipient');
    this.notBillableCtrl = <FormControl>this.infoForm.get('notBillable');
    this.notBillableReasonCtrl = <FormControl>this.infoForm.get('notBillableReason');

    this.patchInfo(this.applicationState.application);
    Some(this.applicationState.application.invoiceRecipientId).do(id => this.findAndPatchCustomer(id));
    this.notBillableCtrl.valueChanges.subscribe(value => this.onNotBillableChange(value));
  }

  onSubmit(): void {
    this.saveApplicationInfo()
      .subscribe(
        rows => NotificationService.message(findTranslation('invoice.action.save')),
        error => NotificationService.errorMessage(error));
  }

  cancel(): void {
    this.infoForm.reset();
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
    return NumberUtil.isDefined(this.recipientForm.value.id);
  }

  private patchInfo(application: Application): void {
    this.infoForm.patchValue({
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
      this.infoForm.removeControl('invoiceRecipient');
    } else {
      this.notBillableReasonCtrl.clearValidators();
      this.infoForm.addControl('invoiceRecipient', this.recipientForm);
    }
  }

  private disableCustomerEdit(): void {
    Object.keys(this.recipientForm.controls)
      .filter(key => ALWAYS_ENABLED_FIELDS.indexOf(key) < 0)
      .forEach(key => this.recipientForm.get(key).disable({emitEvent: false}));
  }

  private saveApplicationInfo(): Observable<Application> {
    let application = this.applicationState.application;

    const invoicingInfo: InvoicingInfoForm = this.infoForm.getRawValue();
    application.notBillable = invoicingInfo.notBillable;
    application.notBillableReason = invoicingInfo.notBillable ? invoicingInfo.notBillableReason : undefined;


    return this.saveCustomer()
      .switchMap(customer => {
        application.invoiceRecipientId = customer.id;
        return this.applicationState.save(application);
    });
  }

  private saveCustomer(): Observable<Customer> {
    const customer = CustomerForm.toCustomer(this.recipientForm.getRawValue());
    if (this.recipientForm.dirty) {
      return this.customerHub.saveCustomer(customer);
    } else {
      return Observable.of(customer);
    }
  }
}
