
import {PostalAddress} from '../common/postal-address';

export class Applicant {

  constructor()
  constructor(
    id: number,
    type: string,
    representative: boolean,
    name: string,
    registryKey: string,
    postalAddress: PostalAddress,
    email: string,
    phone: string)
  constructor(
    public id?: number,
    public type?: string,
    public representative?: boolean,
    public name?: string,
    public registryKey?: string,
    public postalAddress?: PostalAddress,
    public email?: string,
    public phone?: string) {
    this.postalAddress = postalAddress || new PostalAddress();
  }
}
