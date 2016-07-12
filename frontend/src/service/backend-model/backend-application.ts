import {BackendProject} from './backend-project';
import {BackendApplicant} from './backend-applicant';
import {BackendContact} from './backend-contact';
import {BackendLocation} from './backend-location';
import {BackendStructureMeta} from './backend-structure-meta';

export interface BackendApplication {
  id: number;
  project: BackendProject;
  handler: string;
  status: string;
  type: string;
  name: string;
  event: any;
  metadata: BackendStructureMeta;
  creationTime: string;
  applicant: BackendApplicant;
  contactList: Array<BackendContact>;
  location: BackendLocation;
}
