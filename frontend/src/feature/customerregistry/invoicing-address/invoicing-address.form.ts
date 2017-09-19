import {FormBuilder, Validators} from '@angular/forms';
import {PostalAddress} from '../../../model/common/postal-address';
import {InvoicingAddress} from '../../../model/customer/invoicing-address';
import {CustomerType} from '../../../model/customer/customer-type';
import {emailValidator, postalCodeValidator} from '../../../util/complex-validator';

export class InvoicingAddressForm {
  constructor(
    public id?: number,
    public type?: string,
    public name?: string,
    public registryKey?: string,
    public country?: string,
    public postalAddress?: PostalAddress,
    public email?: string,
    public phone?: string,
    public noInvoicing?: boolean) {
    this.postalAddress = postalAddress || new PostalAddress();
  }

  static fromInvoicingAddress(invoicingAddress: InvoicingAddress): InvoicingAddressForm {
    return new InvoicingAddressForm(
      invoicingAddress.id,
      invoicingAddress.type ? CustomerType[invoicingAddress.type] : undefined,
      invoicingAddress.name,
      invoicingAddress.registryKey,
      'Suomi',
      invoicingAddress.postalAddress || new PostalAddress(),
      invoicingAddress.email,
      invoicingAddress.phone,
      invoicingAddress.noInvoicing
    );
  }

  static toInvoicingAddress(form: InvoicingAddressForm): InvoicingAddress {
    let address = new InvoicingAddress();
    address.id = form.id;
    address.type = form.type ? CustomerType[form.type] : undefined;
    address.name = form.name;
    address.registryKey = form.registryKey;
    address.postalAddress = form.postalAddress;
    address.email = form.email;
    address.phone = form.phone;
    address.noInvoicing = form.noInvoicing;
    return address;
  }

  static initialForm(fb: FormBuilder): any {
    return fb.group({
      id: undefined,
      type: [undefined],
      name: ['', [Validators.required, Validators.minLength(2)]],
      registryKey: ['', [Validators.required, Validators.minLength(2)]],
      country: ['Suomi'],
      postalAddress: fb.group({
        streetAddress: [''],
        postalCode: ['', postalCodeValidator],
        city: ['']
      }),
      email: ['', emailValidator],
      phone: ['', Validators.minLength(2)]
    });
  }
}
