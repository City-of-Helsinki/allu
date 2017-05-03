import {Component, OnInit, Input} from '@angular/core';
import {MdDialogRef} from '@angular/material';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ApplicantType} from '../../../../model/application/applicant/applicant-type';
import {EnumUtil} from '../../../../util/enum.util';
import {InvoicingInfoForm} from './invoicing-info.form';
import {InvoicePartition} from '../../../../model/application/invoice/ivoice-partition';

@Component({
  selector: 'invoicing-info',
  template: require('./invoicing-info.component.html'),
  styles: []
})
export class InvoicingInfoComponent implements OnInit {

  @Input() parentForm: FormGroup;

  applicantTypes = EnumUtil.enumValues(ApplicantType);
  invoicePartitions = EnumUtil.enumValues(InvoicePartition);
  invoicingInfoForm: FormGroup;
  invoicingAddressForm: FormGroup;

  constructor(private fb: FormBuilder) {
    // TODO: Get invoicing info if already invoiced / otherwise get invoicing address from applicant
    this.invoicingInfoForm = InvoicingInfoForm.initialForm(fb);
    this.invoicingAddressForm = <FormGroup>this.invoicingInfoForm.get('invoicingAddress');
  }

  ngOnInit(): void {
    this.parentForm.addControl('invoicingInfo', this.invoicingInfoForm);
  }
}
