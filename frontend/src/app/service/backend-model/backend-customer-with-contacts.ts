import {BackendCustomer} from './backend-customer';
import {BackendContact} from './backend-contact';

export interface BackendCustomerWithContacts {
  roleType: string;
  customer: BackendCustomer;
  contacts: Array<BackendContact>;
}
