import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {postalCodeValidator} from '../../util/complex-validator';
import {Some} from '../../util/option';
import {ApplicantType} from '../../model/application/applicant/applicant-type';
import {EnumUtil} from '../../util/enum.util';

@Component({
  selector: 'invoicing-address',
  template: require('./invoicing-address.component.html'),
  styles: []
})
export class InvoicingAddressComponent implements OnInit {
  @Input() parentForm: FormGroup;

  invoicingAddressForm: FormGroup;
  applicantTypes = EnumUtil.enumValues(ApplicantType);

  constructor(private fb: FormBuilder) {
    this.invoicingAddressForm = this.fb.group({
      id: undefined,
      type: [undefined, Validators.required],
      name: ['', [Validators.required, Validators.minLength(2)]],
      registryKey: ['', [Validators.required, Validators.minLength(2)]],
      postalAddress: this.fb.group({
        streetAddress: [''],
        postalCode: ['', postalCodeValidator],
        city: ['']
      }),
      invoiced: [true]
    });

    Some(this.parentForm).do(parent => parent.addControl('invoicing', this.invoicingAddressForm));
  }

  ngOnInit(): void {
  }
}
