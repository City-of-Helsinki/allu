import {Injectable} from '@angular/core';
import {ErrorHandler} from '../error/error-handler.service';
import {AuthHttp} from 'angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {CustomerMapper} from '../mapper/customer-mapper';
import {findTranslation} from '../../util/translations';
import {ContactMapper} from '../mapper/contact-mapper';
import {Contact} from '../../model/customer/contact';
import {Some} from '../../util/option';
import {CustomerQueryParametersMapper, CustomerSearchQuery} from '../mapper/query/customer-query-parameters-mapper';
import {Customer} from '../../model/customer/customer';
import {CustomerWithContacts} from '../../model/customer/customer-with-contacts';

const CUSTOMERS_URL = '/api/customers';
const CUSTOMERS_SEARCH_URL = CUSTOMERS_URL + '/search';
const CONTACTS_FOR_CUSTOMER_URL = CUSTOMERS_URL + '/:customerId/contacts';
const WITH_CONTACTS = '/withcontacts';
const CONTACTS_URL = '/api/contacts';

@Injectable()
export class CustomerService {

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
  }

  public searchCustomersBy(searchQuery: CustomerSearchQuery): Observable<Array<Customer>> {
    return this.authHttp.post(CUSTOMERS_SEARCH_URL, JSON.stringify(CustomerQueryParametersMapper.mapFrontend(searchQuery)))
      .map(response => response.json())
      .map(customers => customers.map(c => CustomerMapper.mapBackend(c)))
      .catch(error => this.errorHandler.handle(error, findTranslation('customer.error.fetch')));
  }

  public fetchAllCustomers(): Observable<Array<Customer>> {
    return this.authHttp.get(CUSTOMERS_URL)
      .map(response => response.json())
      .map(customers => customers.map(c => CustomerMapper.mapBackend(c)))
      .catch(error => this.errorHandler.handle(error, findTranslation('customer.error.fetch')));
  }

  public findCustomerById(id: number): Observable<Customer> {
    let url = CUSTOMERS_URL + '/' + id;
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(customer => CustomerMapper.mapBackend(customer))
      .catch(error => this.errorHandler.handle(error, findTranslation('customer.error.fetch')));
  }

  public findByCustomerIds(ids: Array<number>): Observable<Array<Customer>> {
    let url = CUSTOMERS_URL + '/findByIds';
    return this.authHttp.post(url, JSON.stringify(ids))
      .map(response => response.json())
      .map(customers => customers.map(c => CustomerMapper.mapBackend(c)))
      .catch(error => this.errorHandler.handle(error, findTranslation('customer.error.fetch')));
  }

  public findContactById(id: number): Observable<Contact> {
    let url = CONTACTS_URL + '/' + id;
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(contact => ContactMapper.mapBackend(contact))
      .catch(error => this.errorHandler.handle(error, findTranslation('contact.error.fetch')));
  }

  public findCustomerContacts(customerId: number): Observable<Array<Contact>> {
    let url = CONTACTS_FOR_CUSTOMER_URL.replace(':customerId', String(customerId));
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(contacts => contacts.map(contact => ContactMapper.mapBackend(contact)))
      .catch(error => this.errorHandler.handle(error, findTranslation('customer.error.fetchContacts')));
  }

  public saveCustomerWithContacts(customerWithContacts: CustomerWithContacts): Observable<CustomerWithContacts> {
     return Some(customerWithContacts.customerId)
     .map(id => this.updateCustomer(id, customerWithContacts))
     .orElse(this.createCustomer(customerWithContacts))
     .catch(error => this.errorHandler.handle(error, findTranslation('customer.error.save')));
  }

  private updateCustomer(customerId: number, customer: CustomerWithContacts): Observable<CustomerWithContacts> {
    let url = CUSTOMERS_URL + '/' + customerId + WITH_CONTACTS;
    return this.authHttp.put(url, JSON.stringify(CustomerMapper.mapFrontendWithContacts(customer)))
      .map(response => response.json())
      .map(customerWithContacts => CustomerMapper.mapBackendWithContacts(customerWithContacts));
  }

  private createCustomer(customer: CustomerWithContacts): Observable<CustomerWithContacts> {
    let url = CUSTOMERS_URL + WITH_CONTACTS;
    return this.authHttp.post(url, JSON.stringify(CustomerMapper.mapFrontendWithContacts(customer)))
      .map(response => response.json())
      .map(customerWithContacts => CustomerMapper.mapBackendWithContacts(customerWithContacts));
  }
}
