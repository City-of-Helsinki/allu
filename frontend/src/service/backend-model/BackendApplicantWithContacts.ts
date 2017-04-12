import {BackendApplicant} from './backend-applicant';
import {BackendContact} from './backend-contact';

export interface BackendApplicantWithContacts {
  applicant: BackendApplicant;
  contacts: Array<BackendContact>;
}
