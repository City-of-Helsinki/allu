import {Injectable} from '@angular/core';
import {ErrorHandler} from '../error/error-handler.service';
import {AuthHttp} from 'angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {Applicant} from '../../model/application/applicant/applicant';
import {ApplicantMapper} from '../mapper/applicant-mapper';
import {findTranslation} from '../../util/translations';
import {ContactMapper} from '../mapper/contact-mapper';
import {ApplicantWithContacts} from '../../model/application/applicant/applicant-with-contacts';
import {Contact} from '../../model/application/contact';
import {NumberUtil} from '../../util/number.util';
import {Some} from '../../util/option';
import {HttpResponse, HttpStatus} from '../../util/http-response';
import {HttpUtil} from '../../util/http.util';
import {CustomerQueryParametersMapper, CustomerSearchQuery} from '../mapper/query/customer-query-parameters-mapper';

const APPLICANTS_URL = '/api/applicants';
const APPLICANTS_SEARCH_URL = APPLICANTS_URL + '/search';
const CONTACTS_FOR_APPLICANT_URL = APPLICANTS_URL + '/:applicantId/contacts';
const WITH_CONTACTS = '/withcontacts';
const CONTACTS_URL = '/api/contacts';

@Injectable()
export class CustomerService {

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
  }

  public searchApplicantsBy(searchQuery: CustomerSearchQuery): Observable<Array<Applicant>> {
    return this.authHttp.post(APPLICANTS_SEARCH_URL, JSON.stringify(CustomerQueryParametersMapper.mapFrontend(searchQuery)))
      .map(response => response.json())
      .map(applicants => applicants.map(a => ApplicantMapper.mapBackend(a)))
      .catch(error => this.errorHandler.handle(error, findTranslation('applicant.error.fetch')));
  }

  public fetchAllApplicants(): Observable<Array<Applicant>> {
    return this.authHttp.get(APPLICANTS_URL)
      .map(response => response.json())
      .map(applicants => applicants.map(a => ApplicantMapper.mapBackend(a)))
      .catch(error => this.errorHandler.handle(error, findTranslation('applicant.error.fetch')));
  }

  public findApplicantById(id: number): Observable<Applicant> {
    let url = APPLICANTS_URL + '/' + id;
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(applicant => ApplicantMapper.mapBackend(applicant))
      .catch(error => this.errorHandler.handle(error, findTranslation('applicant.error.fetch')));
  }

  public findContactById(id: number): Observable<Contact> {
    let url = CONTACTS_URL + '/' + id;
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(contact => ContactMapper.mapBackend(contact))
      .catch(error => this.errorHandler.handle(error, findTranslation('contact.error.fetch')));
  }

  public findApplicantContacts(applicantId: number): Observable<Array<Contact>> {
    let url = CONTACTS_FOR_APPLICANT_URL.replace(':applicantId', String(applicantId));
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(contacts => contacts.map(contact => ContactMapper.mapBackend(contact)))
      .catch(error => this.errorHandler.handle(error, findTranslation('applicant.error.fetchContacts')));
  }

  public saveApplicantWithContacts(applicantId: number, applicant: Applicant, contacts: Array<Contact>): Observable<ApplicantWithContacts> {
     let applicantWithContacts = new ApplicantWithContacts(applicant, contacts);
     return Some(applicantId)
     .map(id => this.updateApplicant(id, applicantWithContacts))
     .orElse(this.createApplicant(applicantWithContacts))
     .catch(error => this.errorHandler.handle(error, findTranslation('applicant.error.save')));
  }

  private updateApplicant(applicantId: number, applicant: ApplicantWithContacts): Observable<ApplicantWithContacts> {
    let url = APPLICANTS_URL + '/' + applicantId + WITH_CONTACTS;
    return this.authHttp.put(url, JSON.stringify(ApplicantMapper.mapFrontendWithContacts(applicant)))
      .map(response => response.json())
      .map(applicantWithContacts => ApplicantMapper.mapBackendWithContacts(applicantWithContacts));
  }

  private createApplicant(applicant: ApplicantWithContacts): Observable<ApplicantWithContacts> {
    let url = APPLICANTS_URL + WITH_CONTACTS;
    return this.authHttp.post(url, JSON.stringify(ApplicantMapper.mapFrontendWithContacts(applicant)))
      .map(response => response.json())
      .map(applicantWithContacts => ApplicantMapper.mapBackendWithContacts(applicantWithContacts));
  }
}
