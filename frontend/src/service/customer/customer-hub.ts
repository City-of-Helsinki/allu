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
   * Saves applicant with his / hers contacts
   */
  public saveApplicantWithContacts = (applicantId: number, applicant: Applicant, contacts: Array<Contact>) =>
    this.customerService.saveApplicantWithContacts(applicantId, applicant, contacts);

  public removeApplicant = (applicantId: number) => this.customerService.removeApplicant(applicantId);

  /**
   * Fetches all contacts of given applicant
   */
  public findApplicantContacts = (applicantId: number) => this.customerService.findApplicantContacts(applicantId);

  /**
   * Removes contact by id
   */
  public removeContact = (contactId: number) => this.customerService.removeContact(contactId);
}
