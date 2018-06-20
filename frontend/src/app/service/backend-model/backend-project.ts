import {BackendCustomer} from './backend-customer';
import {BackendContact} from './backend-contact';

export interface BackendProject {
  id: number;
  name: string;
  identifier: string;
  startTime: string;
  endTime: string;
  cityDistricts: Array<number>;
  customer: BackendCustomer;
  contact: BackendContact;
  customerReference: string;
  additionalInfo: string;
  parentId: number;
}

export interface SearchResultProject {
  id: number;
  identifier: string;
}
