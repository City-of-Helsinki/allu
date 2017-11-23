import {PostalAddress} from '../../../model/common/postal-address';
import {FormBuilder, Validators} from '@angular/forms';
import {emailValidator, postalCodeValidator} from '../../../util/complex-validator';
import {Customer} from '../../../model/customer/customer';

export class CustomerForm {
  constructor(
    public id?: number,
    public type?: string,
    public name?: string,
    public registryKey?: string,
    public ovt?: string,
    public country?: string,
    public postalAddress?: PostalAddress,
    public email?: string,
    public phone?: string,
    public active = true,
    public readonly sapCustomerNumber?: string,
    public readonly invoicingProhibited = false
  ) {}

  static fromCustomer(customer: Customer): CustomerForm {
    return new CustomerForm(
      customer.id,
      customer.type,
      customer.name,
      customer.registryKey,
      customer.ovt,
      'Suomi',
      customer.postalAddress || new PostalAddress(),
      customer.email,
      customer.phone,
      customer.active,
      customer.sapCustomerNumber,
      customer.invoicingProhibited
    );
  }

  static toCustomer(form: CustomerForm): Customer {
    let customer = new Customer();
    customer.id = form.id;
    customer.type = form.type;
    customer.name = form.name;
    customer.registryKey = form.registryKey;
    customer.ovt = form.ovt;
    customer.postalAddress = form.postalAddress;
    customer.email = form.email;
    customer.phone = form.phone;
    customer.active = form.active;
    return customer;
  }

  static initialForm(fb: FormBuilder): any {
    return fb.group({
      id: undefined,
      type: [undefined, Validators.required],
      representative: [undefined],
      detailsId: undefined,
      name: ['', [Validators.required, Validators.minLength(2)]],
      registryKey: ['', [Validators.required, Validators.minLength(2)]],
      ovt: ['', [Validators.minLength(12), Validators.maxLength(17)]],
      country: ['Suomi'],
      postalAddress: fb.group({
        streetAddress: [''],
        postalCode: ['', postalCodeValidator],
        city: ['']
      }),
      email: ['', emailValidator],
      phone: ['', Validators.minLength(2)],
      active: [true],
      sapCustomerNumber: [{value: '', disabled: true}],
      invoicingProhibited: [{value: false, disabled: true}]
    });
  }
}
