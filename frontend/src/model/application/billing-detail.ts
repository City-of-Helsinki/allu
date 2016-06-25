import {PostalAddress} from '../common/postal-address';

export class BillingDetail {
  constructor(
    public type: string,
    public country: string,
    public postalAddress: PostalAddress,
    public workNumber: string,
    public reference: string) {}
}
