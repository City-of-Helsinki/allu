import {PostalAddress} from '../common/postal-address';
import {ApplicantDetails} from './applicant-details';

export class Organization implements ApplicantDetails {
  public identifier: string;

  constructor()
  constructor(
    id: number,
    name: string,
    businessId: string,
    postalAddress: PostalAddress,
    email: string,
    phone: string)
  constructor(
    public id?: number,
    public name?: string,
    public businessId?: string,
    public postalAddress?: PostalAddress,
    public email?: string,
    public phone?: string) {
    this.identifier = businessId;
  }
}
