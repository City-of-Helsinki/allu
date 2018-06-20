import {BackendCustomer, SearchResultCustomer} from './backend-customer';
import {BackendContact} from './backend-contact';

export interface BackendCustomerWithContacts {
  roleType: string;
  customer: BackendCustomer;
  contacts: Array<BackendContact>;
}

export interface SearchResultCustomerWithContacts {
  customer: SearchResultCustomer;
}

export interface SearchResultCustomersWithContacts {
  applicant: SearchResultCustomerWithContacts;
}
