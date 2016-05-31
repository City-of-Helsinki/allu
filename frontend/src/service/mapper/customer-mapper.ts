import {BackendCustomer} from '../backend-model/backend-customer';
import {Customer} from '../../model/customer/customer';
import {PostalAddress} from '../../model/common/postal-address';

export class CustomerMapper {

  public static mapBackend(backendCustomer: BackendCustomer): Customer {
    let addr = new PostalAddress(backendCustomer.address, backendCustomer.postOffice, backendCustomer.zipCode, backendCustomer.city);
    return new Customer(backendCustomer.id, backendCustomer.name, backendCustomer.type, addr, backendCustomer.email, backendCustomer.ssn);
  }

  public static mapFrontend(customer: Customer): BackendCustomer {
    return {
      id: undefined,
      name: customer.name,
      type: undefined,
      address: undefined,
      postOffice: undefined,
      zipCode: undefined,
      city: undefined,
      email: undefined,
      ssn: undefined
    };
  }
}
