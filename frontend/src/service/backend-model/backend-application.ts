import {BackendCustomer} from './backend-customer';
import {BackendProject} from './backend-project';
import {BackendApplicant} from './backend-applicant';
import {BackendContact} from './backend-contact';
import {BackendLocation} from './backend-location';
import {BackendBillingDetail} from './backend-billing-detail';
import {BackendSales} from './backend-sales';
import {BackendPricing} from './backend-pricing';
import {BackendStructure} from './backend-structure';

export interface BackendApplication {
  id: number;
  project: BackendProject;
  handler: string;
  customer: BackendCustomer;
  status: string;
  type: string;
  name: string;
  billingDetail: BackendBillingDetail;
  sales: BackendSales;
  event: any;
  pricing: BackendPricing;
  structure: BackendStructure;
  creationTime: string;
  applicant: BackendApplicant;
  contactList: Array<BackendContact>;
  location: BackendLocation;
  comments: string;
}
