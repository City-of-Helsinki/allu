import {BackendProject} from './backend-project';
import {BackendApplicant} from './backend-applicant';
import {BackendContact} from './backend-contact';
import {BackendLocation} from './backend-location';
import {BackendStructureMeta} from './backend-structure-meta';
import {BackendAttachmentInfo} from './backend-attachment-info';

export interface BackendApplication {
  id: number;
  applicationId: string;
  project: BackendProject;
  handler: string;
  status: string;
  type: string;
  name: string;
  event: any;
  metadata: BackendStructureMeta;
  creationTime: string;
  startTime: string;
  endTime: string;
  applicant: BackendApplicant;
  contactList: Array<BackendContact>;
  location: BackendLocation;
  attachmentList: Array<BackendAttachmentInfo>;
}
