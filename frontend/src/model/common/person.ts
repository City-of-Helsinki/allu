import {PostalAddress} from '../common/postal-address';
import {ApplicantDetails} from './applicant-details';

export class Person implements ApplicantDetails {
  public identifier: string;

  constructor(
    public id: number,
    public name: string,
    public ssn: string,
    public postalAddress: PostalAddress,
    public email: string,
    public phone: string) {
    this.identifier = ssn;
  }
}
