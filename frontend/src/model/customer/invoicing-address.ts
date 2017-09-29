import {PostalAddress} from '../common/postal-address';
import {CustomerType} from './customer-type';

export class InvoicingAddress {
  constructor(
    public id?: number,
    public type?: CustomerType,
    public name?: string,
    public registryKey?: string,
    public postalAddress?: PostalAddress,
    public email?: string,
    public phone?: string) {
    this.postalAddress = postalAddress || new PostalAddress();
  }
}
