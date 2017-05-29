import {PostalAddress} from '../common/postal-address';

export class Customer {
  constructor(
    public id?: number,
    public type?: string,
    public name?: string,
    public registryKey?: string,
    public postalAddress?: PostalAddress,
    public email?: string,
    public phone?: string,
    public active = true) {
    this.postalAddress = postalAddress || new PostalAddress();
  }
}
