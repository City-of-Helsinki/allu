import {Injectable} from '@angular/core';
import {ErrorHandler} from '../error/error-handler.service';
import {Observable} from 'rxjs';
import {CustomerMapper} from '../mapper/customer-mapper';
import {findTranslation} from '@util/translations';
import {ContactMapper} from '../mapper/contact-mapper';
import {Contact} from '@model/customer/contact';
import {Some} from '@util/option';
import {CustomerQueryParametersMapper} from '@service/mapper/query/customer-query-parameters-mapper';
import {Customer} from '@model/customer/customer';
import {CustomerWithContacts} from '@model/customer/customer-with-contacts';
import {QueryParametersMapper} from '@service/mapper/query/query-parameters-mapper';
import {PageMapper} from '@service/common/page-mapper';
import {CustomerSearchQuery} from './customer-search-query';
import {PageRequest} from '@model/common/page-request';
import {Sort} from '@model/common/sort';
import {Page} from '@model/common/page';
import {BackendPage} from '@service/backend-model/backend-page';
import {BackendCustomer} from '@service/backend-model/backend-customer';
import {HttpClient} from '@angular/common/http';
import {BackendContact} from '@service/backend-model/backend-contact';
import {BackendCustomerWithContacts} from '@service/backend-model/backend-customer-with-contacts';
import {catchError, map} from 'rxjs/internal/operators';
import {CustomerType} from '@model/customer/customer-type';

const CUSTOMERS_URL = '/api/customers';
const CUSTOMERS_SEARCH_URL = CUSTOMERS_URL + '/search';
const CONTACTS_FOR_CUSTOMER_URL = CUSTOMERS_URL + '/:customerId/contacts';
const WITH_CONTACTS = '/withcontacts';

@Injectable()
export class CustomerService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  public pagedSearch(searchQuery: CustomerSearchQuery, sort?: Sort, pageRequest?: PageRequest): Observable<Page<Customer>> {
    return this.http.post<BackendPage<BackendCustomer>>(
      CUSTOMERS_SEARCH_URL,
      JSON.stringify(CustomerQueryParametersMapper.mapFrontend(searchQuery)),
      {params: QueryParametersMapper.mapPageRequest(pageRequest, sort, searchQuery.matchAny)}).pipe(
      map(page => PageMapper.mapBackend(page, CustomerMapper.mapBackend)),
      catchError(error => this.errorHandler.handle(error, findTranslation('customer.error.fetch')))
    );
  }

  public search(searchQuery: CustomerSearchQuery, sort?: Sort, pageRequest?: PageRequest): Observable<Array<Customer>> {
    return this.pagedSearch(searchQuery, sort, pageRequest).pipe(
      map(page => page.content)
    );
  }

  public pagedSearchByType(type: CustomerType, searchQuery: CustomerSearchQuery,
                      sort?: Sort, pageRequest?: PageRequest): Observable<Page<Customer>> {
    const query = {...searchQuery, type: undefined};
    const url = `${CUSTOMERS_SEARCH_URL}/${type}`;
    return this.http.post<BackendPage<BackendCustomer>>(
      url, JSON.stringify(CustomerQueryParametersMapper.mapFrontend(query)),
      {params: QueryParametersMapper.mapPageRequest(pageRequest, sort, query.matchAny)}).pipe(
      map(page => PageMapper.mapBackend(page, CustomerMapper.mapBackend)),
      catchError(error => this.errorHandler.handle(error, findTranslation('customer.error.fetch')))
    );
  }

  public findCustomerById(id: number): Observable<Customer> {
    const url = CUSTOMERS_URL + '/' + id;
    return this.http.get<BackendCustomer>(url).pipe(
      map(customer => CustomerMapper.mapBackend(customer)),
      catchError(error => this.errorHandler.handle(error, findTranslation('customer.error.fetch')))
    );
  }

  public findByCustomerIds(ids: Array<number>): Observable<Array<Customer>> {
    const url = CUSTOMERS_URL + '/findByIds';
    return this.http.post<BackendCustomer[]>(url, JSON.stringify(ids)).pipe(
      map(customers => customers.map(c => CustomerMapper.mapBackend(c))),
      catchError(error => this.errorHandler.handle(error, findTranslation('customer.error.fetch')))
    );
  }

  public findCustomerContacts(customerId: number): Observable<Array<Contact>> {
    const url = CONTACTS_FOR_CUSTOMER_URL.replace(':customerId', String(customerId));
    return this.http.get<BackendContact[]>(url).pipe(
      map(contacts => contacts.map(contact => ContactMapper.mapBackend(contact))),
      catchError(error => this.errorHandler.handle(error, findTranslation('customer.error.fetchContacts')))
    );
  }

  findCustomerActiveContacts(customerId: number): Observable<Array<Contact>> {
    return this.findCustomerContacts(customerId).pipe(
      map(contacts => contacts.filter(c => c.active))
    );
  }

  public saveCustomer(customer: Customer): Observable<Customer> {
    return Some(customer.id)
      .map(id => this.updateCustomer(id, customer))
      .orElseGet(() => this.createCustomer(customer)).pipe(
        catchError(error => this.errorHandler.handle(error, findTranslation('customer.error.save')))
      );
  }

  public saveContactsForCustomer(customerId: number, contacts: Array<Contact>): Observable<CustomerWithContacts> {
    return this.saveCustomerWithContacts(new CustomerWithContacts(undefined, undefined, contacts));
  }

  public saveCustomerWithContacts(customerWithContacts: CustomerWithContacts): Observable<CustomerWithContacts> {
     return Some(customerWithContacts.customerId)
     .map(id => this.updateCustomerWithContacts(id, customerWithContacts))
     .orElseGet(() => this.createCustomerWithContacts(customerWithContacts)).pipe(
       catchError(error => this.errorHandler.handle(error, findTranslation('customer.error.save')))
     );
  }

  private updateCustomer(id: number, customer: Customer): Observable<Customer> {
    const url = CUSTOMERS_URL + '/' + id;
    return this.http.put<BackendCustomer>(url, JSON.stringify(CustomerMapper.mapFrontend(customer))).pipe(
      map(saved => CustomerMapper.mapBackend(saved))
    );
  }

  private createCustomer(customer: Customer): Observable<Customer> {
    return this.http.post<BackendCustomer>(CUSTOMERS_URL, JSON.stringify(CustomerMapper.mapFrontend(customer))).pipe(
      map(saved => CustomerMapper.mapBackend(saved))
    );
  }

  private updateCustomerWithContacts(customerId: number, customer: CustomerWithContacts): Observable<CustomerWithContacts> {
    const url = CUSTOMERS_URL + '/' + customerId + WITH_CONTACTS;
    return this.http.put<BackendCustomerWithContacts>(url, JSON.stringify(CustomerMapper.mapFrontendWithContacts(customer))).pipe(
      map(customerWithContacts => CustomerMapper.mapBackendWithContacts(customerWithContacts))
    );
  }

  private createCustomerWithContacts(customer: CustomerWithContacts): Observable<CustomerWithContacts> {
    const url = CUSTOMERS_URL + WITH_CONTACTS;
    return this.http.post<BackendCustomerWithContacts>(url, JSON.stringify(CustomerMapper.mapFrontendWithContacts(customer))).pipe(
      map(customerWithContacts => CustomerMapper.mapBackendWithContacts(customerWithContacts))
    );
  }
}
