import {BackendProject} from './backend-project';
import {BackendApplicant} from './backend-applicant';
import {BackendContact} from './backend-contact';
import {BackendLocation} from './backend-location';
import {BackendStructureMeta} from './backend-structure-meta';
import {BackendAttachmentInfo} from './backend-attachment-info';
import {BackendUser} from './backend-user';

export interface BackendApplication {
  id: number;
  applicationId: string;
  project: BackendProject;
  handler: BackendUser;
  status: string;
  type: string;
  specifiers: Array<string>;
  name: string;
  event: any;
  metadata: BackendStructureMeta;
  creationTime: string;
  startTime: string;
  endTime: string;
  applicant: BackendApplicant;
  contactList: Array<BackendContact>;
  location: BackendLocation;
  calculatedPrice: number;
  priceOverride: number;
  priceOverrideReason: string;
  attachmentList: Array<BackendAttachmentInfo>;
}
