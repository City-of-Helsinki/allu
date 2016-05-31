import {PostalAddress} from '../common/postal-address';

export class Person {
  constructor(public name: string, public ssn: string, public postalAddress: PostalAddress, public phone: string, public email: string) {};
}
