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
import {InvoicingAddressForm} from '../../../customerregistry/invoicing-address/invoicing-address.form';
import {NotificationService} from '../../../../service/notification/notification.service';
import {findTranslation} from '../../../../util/translations';
import {Observable} from 'rxjs/Observable';

@Component({
  selector: 'invoicing-info',
  template: require('./invoicing-info.component.html'),
  styles: []
})
export class InvoicingInfoComponent implements OnInit {

  customerTypes = EnumUtil.enumValues(CustomerType);
  invoicePartitions = EnumUtil.enumValues(InvoicePartition);
  infoForm: FormGroup;
  addressForm: FormGroup;

  private notBillableCtrl: FormControl;
  private notBillableReasonCtrl: FormControl;

  constructor(private fb: FormBuilder, private applicationState: ApplicationState, private customerHub: CustomerHub) {
  }

  ngOnInit(): void {
    this.infoForm = InvoicingInfoForm.initialForm(this.fb);
    this.addressForm = <FormGroup>this.infoForm.get('invoicingAddress');
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

  public invoiceRecipientChange(recipient: InvoicingAddressForm) {
    if (recipient.id) {
      this.disableCustomerEdit();
    }
  }

  get billable(): boolean {
    return !this.notBillableCtrl.value;
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
      .subscribe(customer => this.addressForm.patchValue(CustomerForm.fromCustomer(customer)));
  }

  private onNotBillableChange(notBillable: boolean) {
    if (notBillable) {
      this.notBillableReasonCtrl.setValidators([Validators.required]);
      this.infoForm.removeControl('invoicingAddress');
    } else {
      this.notBillableReasonCtrl.clearValidators();
      this.infoForm.addControl('invoicingAddress', this.addressForm);
    }
  }

  private disableCustomerEdit(): void {
    Object.keys(this.addressForm.controls)
      .filter(key => ALWAYS_ENABLED_FIELDS.indexOf(key) < 0)
      .forEach(key => this.addressForm.get(key).disable({emitEvent: false}));
  }

  private saveApplicationInfo(): Observable<Application> {
    let application = this.applicationState.application;

    const invoicingInfo: InvoicingInfoForm = this.infoForm.getRawValue();
    application.notBillable = invoicingInfo.notBillable;
    application.notBillableReason = invoicingInfo.notBillable ? invoicingInfo.notBillableReason : undefined;

    const invoicingAddress: InvoicingAddressForm = this.addressForm.getRawValue();
    application.invoiceRecipientId = Some(invoicingAddress).map(recipient => recipient.id).orElse(undefined);

    return this.applicationState.save(application);
  }
}
