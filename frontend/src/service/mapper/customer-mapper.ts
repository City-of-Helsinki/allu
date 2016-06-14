import {BackendCustomer} from '../backend-model/backend-customer';
import {PersonMapper} from './person-mapper';
import {OrganizationMapper} from './organization-mapper';
import {Customer} from '../../model/common/customer';

export class CustomerMapper {

  public static mapBackend(backendCustomer: BackendCustomer): Customer {
    return (backendCustomer) ?
      new Customer(
        backendCustomer.id,
        backendCustomer.type,
        backendCustomer.sapId,
        PersonMapper.mapBackend(backendCustomer.person),
        OrganizationMapper.mapBackend(backendCustomer.organization)) : undefined;
  }

  public static mapFrontend(customer: Customer): BackendCustomer {
    return (customer) ?
    {
      id: customer.id,
      type: customer.type,
      sapId: customer.sapId,
      person: PersonMapper.mapFrontend(customer.person),
      organization: OrganizationMapper.mapFrontend(customer.organization)
    } : undefined;
  }
}
