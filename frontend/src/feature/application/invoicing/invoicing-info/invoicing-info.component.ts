import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {CustomerType} from '../../../../model/customer/customer-type';
import {EnumUtil} from '../../../../util/enum.util';
import {InvoicingInfoForm} from './invoicing-info.form';
import {InvoicePartition} from '../../../../model/application/invoice/ivoice-partition';
import {ApplicationState} from '../../../../service/application/application-state';
import {CustomerHub} from '../../../../service/customer/customer-hub';
import {Some} from '../../../../util/option';
import {CustomerForm} from '../../../customerregistry/customer/customer.form';

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

  constructor(private fb: FormBuilder, private applicationState: ApplicationState, private customerHub: CustomerHub) {
    this.invoicingInfoForm = InvoicingInfoForm.initialForm(this.fb);
    this.invoicingAddressForm = <FormGroup>this.invoicingInfoForm.get('invoicingAddress');
  }

  ngOnInit(): void {
    this.parentForm.addControl('invoicingInfo', this.invoicingInfoForm);
    Some(this.applicationState.application.invoiceRecipientId).do(id => this.findAndPatchCustomer(id));
  }

  private findAndPatchCustomer(id: number): void {
    this.customerHub.findCustomerById(id)
      .subscribe(customer => this.invoicingAddressForm.patchValue(CustomerForm.fromCustomer(customer)));
  }
}
