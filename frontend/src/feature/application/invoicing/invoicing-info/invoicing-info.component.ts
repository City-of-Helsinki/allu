import {Component, Input, OnInit} from '@angular/core';
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

@Component({
  selector: 'invoicing-info',
  template: require('./invoicing-info.component.html'),
  styles: []
})
export class InvoicingInfoComponent implements OnInit {

  @Input() parentForm: FormGroup;

  customerTypes = EnumUtil.enumValues(CustomerType);
  invoicePartitions = EnumUtil.enumValues(InvoicePartition);
  invoicingInfoForm: FormGroup;
  invoicingAddressForm: FormGroup;

  private notBillableCtrl: FormControl;
  private notBillableReasonCtrl: FormControl;

  constructor(private fb: FormBuilder, private applicationState: ApplicationState, private customerHub: CustomerHub) {
    this.invoicingInfoForm = InvoicingInfoForm.initialForm(this.fb);
    this.invoicingAddressForm = <FormGroup>this.invoicingInfoForm.get('invoicingAddress');
    this.notBillableCtrl = <FormControl>this.invoicingInfoForm.get('notBillable');
    this.notBillableReasonCtrl = <FormControl>this.invoicingInfoForm.get('notBillableReason');
  }

  ngOnInit(): void {
    this.parentForm.addControl('invoicingInfo', this.invoicingInfoForm);
    this.patchInfo(this.applicationState.application);
    Some(this.applicationState.application.invoiceRecipientId).do(id => this.findAndPatchCustomer(id));
    this.notBillableCtrl.valueChanges.subscribe(value => this.onNotBillableChange(value));
  }

  private patchInfo(application: Application): void {
    this.invoicingInfoForm.patchValue({
      notBillable: application.notBillable,
      notBillableReason: application.notBillableReason
    });
  }

  private findAndPatchCustomer(id: number): void {
    this.customerHub.findCustomerById(id)
      .subscribe(customer => this.invoicingAddressForm.patchValue(CustomerForm.fromCustomer(customer)));
  }

  private onNotBillableChange(notBillable: boolean) {
    if (notBillable) {
      this.notBillableReasonCtrl.setValidators([Validators.required]);
    } else {
      this.notBillableReasonCtrl.clearValidators();
    }
  }
}
