import {BackendCustomer} from './backend-customer';
import {BackendProject} from './backend-project';
import {BackendApplicant} from './backend-applicant';
import {BackendContact} from './backend-contact';

export interface BackendApplication {
  id: number;
  project: BackendProject;
  handler: string;
  customer: BackendCustomer;
  status: string;
  type: string;
  name: string;
  creationTime: string;
  applicant: BackendApplicant;
  contactList: Array<BackendContact>;
  location: GeoJSON.FeatureCollection<GeoJSON.GeometryObject>;
}
