import {Injectable} from '@angular/core';
import {CustomerService} from './customer.service';
import {Contact} from '../../model/customer/contact';
import {CustomerSearchQuery} from '../mapper/query/customer-query-parameters-mapper';
import {CustomerWithContacts} from '../../model/customer/customer-with-contacts';
import {Customer} from '../../model/customer/customer';

@Injectable()
export class CustomerHub {

  constructor(private customerService: CustomerService) {}

  /**
   * Searches customers by given search query
   */
  public searchCustomersBy = (searchQuery: CustomerSearchQuery) => this.customerService.searchCustomersBy(searchQuery);

  /**
   * Fetches customers by given ids
   */
  public findByCustomerIds = (ids: Array<number>) => this.customerService.findByCustomerIds(ids);

  /**
   * Fetches customer by given id
   */
  public findCustomerById = (id: number) => this.customerService.findCustomerById(id);

  /**
   * Fetches contact by given id
   */
  public findContactById = (id: number) => this.customerService.findContactById(id);

  /**
   * Saves single customer
   */
  public saveCustomer = (customer: Customer) => this.customerService.saveCustomer(customer);

  /**
   * Saves contacts for given customer
   */
  public saveContactsForCustomer = (customerId: number, contacts: Array<Contact>) =>
    this.customerService.saveCustomerWithContacts(new CustomerWithContacts(undefined, undefined, contacts))

  /**
   * Saves customer with his / hers contacts
   */
  public saveCustomerWithContacts = (customerWithContacts: CustomerWithContacts) =>
    this.customerService.saveCustomerWithContacts(customerWithContacts)

  /**
   * Fetches all contacts of given customer
   */
  public findCustomerActiveContacts = (customerId: number) => this.customerService.findCustomerContacts(customerId)
    .map(contacts => contacts.filter(c => c.active))
}
