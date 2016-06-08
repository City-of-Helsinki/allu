import {PostalAddress} from '../common/postal-address';

export class Person {
  constructor(
    public id: number,
    public name: string,
    public postalAddress: PostalAddress,
    public email: string,
    public phone: string,
    public ssn: string) {};
}
