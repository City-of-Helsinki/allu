import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Some} from '../../../util/option';
import {CustomerType} from '../../../model/customer/customer-type';
import {EnumUtil} from '../../../util/enum.util';
import {CustomerForm} from '../customer/customer.form';

@Component({
  selector: 'invoicing-address',
  templateUrl: './invoicing-address.component.html',
  styleUrls: []
})
export class InvoicingAddressComponent implements OnInit {
  @Input() parentForm: FormGroup;

  invoiceRecipientForm: FormGroup;
  customerTypes = EnumUtil.enumValues(CustomerType);

  constructor(private fb: FormBuilder) {
    this.invoiceRecipientForm = CustomerForm.initialForm(this.fb);
    this.invoiceRecipientForm.addControl('invoiced', this.fb.control(true));

    Some(this.parentForm).do(parent => parent.addControl('invoicing', this.invoiceRecipientForm));
  }

  ngOnInit(): void {
  }
}
