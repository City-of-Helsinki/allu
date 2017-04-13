import {Injectable} from '@angular/core';
import {CustomerService} from './customer.service';
import {ApplicantWithContacts} from '../../model/application/applicant/applicant-with-contacts';
import {Applicant} from '../../model/application/applicant/applicant';
import {Contact} from '../../model/application/contact';
import {CustomerSearchQuery} from '../mapper/query/customer-query-parameters-mapper';

@Injectable()
export class CustomerHub {

  constructor(private customerService: CustomerService) {}

  /**
   * Searches applicants by given search query
   */
  public searchApplicantsBy = (searchQuery: CustomerSearchQuery) => this.customerService.searchApplicantsBy(searchQuery);

  /**
   * Fetches all applicants
   */
  public fetchAllApplicants = () => this.customerService.fetchAllApplicants();

  /**
   * Fetches applicant by given id
   */
  public findApplicantById = (id: number) => this.customerService.findApplicantById(id);

  /**
   * Fetches contact by given id
   */
  public findContactById = (id: number) => this.customerService.findContactById(id);

  /**
   * Saves applicant with his / hers contacts
   */
  public saveApplicantWithContacts = (applicantId: number, applicant: Applicant, contacts: Array<Contact>) =>
    this.customerService.saveApplicantWithContacts(applicantId, applicant, contacts);

  /**
   * Fetches all contacts of given applicant
   */
  public findApplicantActiveContacts = (applicantId: number) => this.customerService.findApplicantContacts(applicantId)
    .map(contacts => contacts.filter(c => c.active));
}
