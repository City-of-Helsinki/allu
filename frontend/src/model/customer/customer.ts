import {BackendCustomer} from '../../service/backend-model/backend-customer';
import {PostalAddress} from '../common/postal-address';

export class Customer {
  constructor(
    public id: number,
    public name: string,
    public type: string,
    public postalAddress: PostalAddress,
    public email: string,
    public ssn: string) {}
}
