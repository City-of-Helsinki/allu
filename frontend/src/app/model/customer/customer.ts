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
    public country: string = 'FI',
    public active = true,
    public sapCustomerNumber?: string,
    public invoicingProhibited = false,
    public invoicingOnly = false,
    public projectIdentifierPrefix?: string) {
    this.postalAddress = postalAddress || new PostalAddress();
  }
}
