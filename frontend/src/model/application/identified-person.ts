import {Person} from './person';
import {PostalAddress} from '../common/postal-address';

export class IdentifiedPerson extends Person {
  constructor(public id: number, name: string, ssn: string, postalAddress: PostalAddress, phone: string, email: string) {
    super(name, ssn, postalAddress, phone, email);
  }
}
