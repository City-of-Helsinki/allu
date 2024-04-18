import {PostalAddress} from '../../../model/common/postal-address';
import {UntypedFormBuilder, Validators} from '@angular/forms';
import {postalCodeValidator} from '../../../util/complex-validator';
import {Customer} from '../../../model/customer/customer';
import {CustomerType} from '@model/customer/customer-type';

export class CustomerForm {
  constructor(
    public id?: number,
    public type?: CustomerType,
    public name?: string,
    public registryKey?: string,
    public ovt?: string,
    public invoicingOperator?: string,
    public country?: string,
    public postalAddress?: PostalAddress,
    public email?: string,
    public phone?: string,
    public active = true,
    public readonly sapCustomerNumber?: string,
    public readonly invoicingProhibited = false,
    public invoicingOnly = false,
    public projectIdentifierPrefix?: string
  ) {}

  static fromCustomer(customer: Customer): CustomerForm {
    return new CustomerForm(
      customer.id,
      customer.type,
      customer.name,
      customer.registryKey,
      customer.ovt,
      customer.invoicingOperator,
      customer.country,
      customer.postalAddress || new PostalAddress(),
      customer.email,
      customer.phone,
      customer.active,
      customer.sapCustomerNumber,
      customer.invoicingProhibited,
      customer.invoicingOnly,
      customer.projectIdentifierPrefix
    );
  }

  static toCustomer(form: CustomerForm): Customer {
    const customer = new Customer();
    customer.id = form.id;
    customer.type = form.type;
    customer.name = form.name;
    customer.registryKey = form.registryKey;
    customer.ovt = form.ovt;
    customer.invoicingOperator = form.invoicingOperator;
    customer.postalAddress = form.postalAddress;
    customer.email = form.email;
    customer.phone = form.phone;
    customer.active = form.active;
    customer.invoicingOnly = form.invoicingOnly;
    customer.country = form.country;
    customer.projectIdentifierPrefix = form.projectIdentifierPrefix;
    return customer;
  }

  static initialForm(fb: UntypedFormBuilder): any {
    return fb.group({
      id: undefined,
      type: [undefined, Validators.required],
      representative: [undefined],
      detailsId: undefined,
      name: ['', [Validators.required, Validators.minLength(2)]],
      registryKey: ['', [Validators.minLength(2)]],
      ovt: ['', [Validators.minLength(12), Validators.maxLength(18)]],
      invoicingOperator: [''],
      country: ['FI', Validators.required],
      postalAddress: fb.group({
        streetAddress: [''],
        postalCode: ['', postalCodeValidator],
        city: ['']
      }),
      email: ['', Validators.email],
      phone: ['', Validators.minLength(2)],
      active: [true],
      sapCustomerNumber: [{value: '', disabled: true}],
      invoicingProhibited: [{value: false, disabled: true}],
      invoicingOnly: [false],
      projectIdentifierPrefix: undefined
    });
  }
}
