import {PostalAddress} from '../common/postal-address';

export class Customer {
  constructor(
    public id?: number,
    public type?: string,
    public name?: string,
    public registryKey?: string,
    public ovt?: string,
    public invoicingOperator?: string,
    public postalAddress?: PostalAddress,
    public email?: string,
    public phone?: string,
    public active = true,
    public sapCustomerNumber?: string,
    public invoicingProhibited = false) {
    this.postalAddress = postalAddress || new PostalAddress();
  }
}
