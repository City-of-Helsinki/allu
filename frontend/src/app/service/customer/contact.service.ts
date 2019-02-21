import {Injectable} from '@angular/core';
import {ErrorHandler} from '../error/error-handler.service';
import {Observable} from 'rxjs';
import {findTranslation} from '@util/translations';
import {ContactMapper} from '../mapper/contact-mapper';
import {Contact} from '@model/customer/contact';
import {HttpClient} from '@angular/common/http';
import {BackendContact} from '@service/backend-model/backend-contact';
import {catchError, map} from 'rxjs/internal/operators';
import {NumberUtil} from '@util/number.util';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';
import {Page} from '@model/common/page';
import {BackendPage} from '@service/backend-model/backend-page';
import {QueryParametersMapper} from '@service/mapper/query/query-parameters-mapper';
import {PageMapper} from '@service/common/page-mapper';
import {ContactQueryParametersMapper} from '@service/mapper/query/contact-query-parameters-mapper';
import {ContactSearchQuery} from '@service/customer/contact-search-query';

const CUSTOMERS_URL = '/api/customers';
const CONTACTS_URL = '/api/contacts';
const CONTACTS_SEARCH_URL = '/api/contacts/search';

@Injectable()
export class ContactService {

  constructor(private http: HttpClient,
              private errorHandler: ErrorHandler) {
  }

  public pagedSearch(searchQuery: ContactSearchQuery, sort?: Sort, pageRequest?: PageRequest): Observable<Page<Contact>> {
    return this.http.post<BackendPage<BackendContact>>(
      CONTACTS_SEARCH_URL,
      JSON.stringify(ContactQueryParametersMapper.mapFrontend(searchQuery)),
      {params: QueryParametersMapper.mapPageRequest(pageRequest, sort)}).pipe(
      map(page => PageMapper.mapBackend(page, ContactMapper.mapBackend)),
      catchError(error => this.errorHandler.handle(error, findTranslation('contact.error.fetch')))
    );
  }

  public search(searchQuery: ContactSearchQuery, sort?: Sort, pageRequest?: PageRequest): Observable<Contact[]> {
    return this.pagedSearch(searchQuery, sort, pageRequest).pipe(
      map(page => page.content)
    );
  }

  public findById(id: number): Observable<Contact> {
    const url = CONTACTS_URL + '/' + id;
    return this.http.get<BackendContact>(url).pipe(
      map(contact => ContactMapper.mapBackend(contact)),
      catchError(error => this.errorHandler.handle(error, findTranslation('contact.error.fetch')))
    );
  }

  public save(customerId: number, contact: Contact): Observable<Contact> {
    if (NumberUtil.isExisting(contact)) {
      return this.update(contact);
    } else {
      return this.create(customerId, contact);
    }
  }

  private create(customerId: number, contact: Contact): Observable<Contact> {
    const url = `${CUSTOMERS_URL}/${customerId}/contacts`;
    return this.http.post<BackendContact>(url, JSON.stringify(ContactMapper.mapFrontend(contact))).pipe(
      map(saved => ContactMapper.mapBackend(saved))
    );
  }

  private update(contact: Contact): Observable<Contact> {
    const url = `${CONTACTS_URL}/${contact.id}`;
    return this.http.put<BackendContact>(url, JSON.stringify(ContactMapper.mapFrontend(contact))).pipe(
      map(saved => ContactMapper.mapBackend(saved))
    );
  }
}
