import {Injectable} from '@angular/core';
import {CustomerService} from './customer.service';
import {Contact} from '../../model/customer/contact';
import {CustomerSearchQuery} from '../mapper/query/customer-query-parameters-mapper';
import {Customer} from '../../model/customer/customer';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';

@Injectable()
export class CustomerHub {

  private orderer$ = new BehaviorSubject<Contact>(undefined);

  constructor(private customerService: CustomerService) {}

  /**
   * Searches customers by given search query
   */
  public searchCustomersBy = (searchQuery: CustomerSearchQuery) => this.customerService.searchCustomersBy(searchQuery);

  /**
   * Fetches all customers
   */
  public fetchAllCustomers = () => this.customerService.fetchAllCustomers();

  /**
   * Fetches customer by given id
   */
  public findCustomerById = (id: number) => this.customerService.findCustomerById(id);

  /**
   * Fetches contact by given id
   */
  public findContactById = (id: number) => this.customerService.findContactById(id);

  /**
   * Saves customer with his / hers contacts
   */
  public saveCustomerWithContacts = (customerId: number, customer: Customer, contacts: Array<Contact>) =>
    this.customerService.saveCustomerWithContacts(customerId, customer, contacts);

  /**
   * Fetches all contacts of given customer
   */
  public findCustomerActiveContacts = (customerId: number) => this.customerService.findCustomerContacts(customerId)
    .map(contacts => contacts.filter(c => c.active));

  /**
   * Emits values of contact id which is currently selected as orderer
   */
  get orderer() {
    return this.orderer$.asObservable().share();
  }

  /**
   * Method to inform that new orderer is selected
   */
  public ordererWasSelected(contact: Contact): void {
    this.orderer$.next(contact);
  }
}
