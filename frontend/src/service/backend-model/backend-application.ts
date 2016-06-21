import {BackendCustomer} from './backend-customer';
import {BackendProject} from './backend-project';
import {BackendApplicant} from './backend-applicant';
import {BackendContact} from './backend-contact';
import {BackendLocation} from './backend-location';

export interface BackendApplication {
  id: number;
  project: BackendProject;
  handler: string;
  customer: BackendCustomer;
  status: string;
  type: string;
  name: string;
  event: any;
  creationTime: string;
  applicant: BackendApplicant;
  contactList: Array<BackendContact>;
  location: BackendLocation;
}
