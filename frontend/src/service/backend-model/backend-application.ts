import {BackendProject} from './backend-project';
import {BackendApplicant} from './backend-applicant';
import {BackendContact} from './backend-contact';
import {BackendLocation} from './backend-location';
import {BackendStructureMeta} from './backend-structure-meta';
import {BackendAttachmentInfo} from './backend-attachment-info';
import {BackendUser} from './backend-user';
import {BackendApplicationTag} from '../mapper/application-tag-mapper';

export interface BackendApplication {
  id: number;
  applicationId: string;
  project: BackendProject;
  handler: BackendUser;
  status: string;
  type: string;
  kind: string;
  metadata: BackendStructureMeta;
  name: string;
  creationTime: string;
  startTime: string;
  endTime: string;
  applicant: BackendApplicant;
  contactList: Array<BackendContact>;
  location: BackendLocation;
  extension: any;
  decisionTime: string;
  attachmentList: Array<BackendAttachmentInfo>;
  calculatedPrice: number;
  priceOverride: number;
  priceOverrideReason: string;
  applicationTags: Array<BackendApplicationTag>;
}
