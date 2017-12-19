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

  public findCustomerById(id: number): Observable<Customer> {
    const url = CUSTOMERS_URL + '/' + id;
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(customer => CustomerMapper.mapBackend(customer))
      .catch(error => this.errorHandler.handle(error, findTranslation('customer.error.fetch')));
  }

  public findByCustomerIds(ids: Array<number>): Observable<Array<Customer>> {
    const url = CUSTOMERS_URL + '/findByIds';
    return this.authHttp.post(url, JSON.stringify(ids))
      .map(response => response.json())
      .map(customers => customers.map(c => CustomerMapper.mapBackend(c)))
      .catch(error => this.errorHandler.handle(error, findTranslation('customer.error.fetch')));
  }

  public findContactById(id: number): Observable<Contact> {
    const url = CONTACTS_URL + '/' + id;
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(contact => ContactMapper.mapBackend(contact))
      .catch(error => this.errorHandler.handle(error, findTranslation('contact.error.fetch')));
  }

  public findCustomerContacts(customerId: number): Observable<Array<Contact>> {
    const url = CONTACTS_FOR_CUSTOMER_URL.replace(':customerId', String(customerId));
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(contacts => contacts.map(contact => ContactMapper.mapBackend(contact)))
      .catch(error => this.errorHandler.handle(error, findTranslation('customer.error.fetchContacts')));
  }

  public saveCustomer(customer: Customer): Observable<Customer> {
    return Some(customer.id)
      .map(id => this.updateCustomer(id, customer))
      .orElse(this.createCustomer(customer))
      .catch(error => this.errorHandler.handle(error, findTranslation('customer.error.save')));
  }

  public saveCustomerWithContacts(customerWithContacts: CustomerWithContacts): Observable<CustomerWithContacts> {
     return Some(customerWithContacts.customerId)
     .map(id => this.updateCustomerWithContacts(id, customerWithContacts))
     .orElse(this.createCustomerWithContacts(customerWithContacts))
     .catch(error => this.errorHandler.handle(error, findTranslation('customer.error.save')));
  }

  private updateCustomer(id: number, customer: Customer): Observable<Customer> {
    const url = CUSTOMERS_URL + '/' + id;
    return this.authHttp.put(url, JSON.stringify(CustomerMapper.mapFrontend(customer)))
      .map(response => response.json())
      .map(saved => CustomerMapper.mapBackend(saved));
  }

  private createCustomer(customer: Customer): Observable<Customer> {
    return this.authHttp.post(CUSTOMERS_URL, JSON.stringify(CustomerMapper.mapFrontend(customer)))
      .map(response => response.json())
      .map(saved => CustomerMapper.mapBackend(saved));
  }

  private updateCustomerWithContacts(customerId: number, customer: CustomerWithContacts): Observable<CustomerWithContacts> {
    const url = CUSTOMERS_URL + '/' + customerId + WITH_CONTACTS;
    return this.authHttp.put(url, JSON.stringify(CustomerMapper.mapFrontendWithContacts(customer)))
      .map(response => response.json())
      .map(customerWithContacts => CustomerMapper.mapBackendWithContacts(customerWithContacts));
  }

  private createCustomerWithContacts(customer: CustomerWithContacts): Observable<CustomerWithContacts> {
    const url = CUSTOMERS_URL + WITH_CONTACTS;
    return this.authHttp.post(url, JSON.stringify(CustomerMapper.mapFrontendWithContacts(customer)))
      .map(response => response.json())
      .map(customerWithContacts => CustomerMapper.mapBackendWithContacts(customerWithContacts));
  }
}
