import {PostalAddress} from '../common/postal-address';

export class Organization {
  constructor(
    public id: number,
    public name: string,
    public businessId: string,
    public postalAddress: PostalAddress,
    public email: string,
    public phone: string) {}
}
