import {FormBuilder} from '@angular/forms';
import {PostalAddress} from '../../../model/common/postal-address';
import {InvoicingAddress} from '../../../model/customer/invoicing-address';
import {CustomerType} from '../../../model/customer/customer-type';
import {CustomerForm} from '../customer/customer.form';

export class InvoicingAddressForm {
  constructor(
    public id?: number,
    public type?: string,
    public name?: string,
    public registryKey?: string,
    public country?: string,
    public postalAddress?: PostalAddress,
    public email?: string,
    public phone?: string) {
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
      invoicingAddress.phone
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
    return address;
  }

  static initialForm(fb: FormBuilder): any {
    return CustomerForm.initialForm(fb);
  }
}
