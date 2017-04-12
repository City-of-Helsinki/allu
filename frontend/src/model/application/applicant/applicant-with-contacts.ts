import {Applicant} from './applicant';
import {Contact} from '../contact';
export class ApplicantWithContacts {
  constructor(
    public applicant?: Applicant,
    public contacts: Array<Contact> = []) {}
}
