import {BackendCustomer} from './backend-customer';
import {BackendProject} from './backend-project';

export interface BackendApplication {
  id: number;
  name: string;
  type: string;
  status: string;
  handler: string;
  information: string;
  createDate: string;
  startDate: string;
  customer: BackendCustomer;
  project: BackendProject;
}
