import {BackendProject} from './backend-project';
import {BackendContact} from './backend-contact';
import {BackendLocation} from './backend-location';
import {BackendAttachmentInfo} from './backend-attachment-info';
import {BackendUser} from './backend-user';
import {BackendApplicationTag} from '../mapper/application-tag-mapper';
import {BackendComment} from '../application/comment/comment-mapper';
import {BackendDistributionEntry} from './backend-distribution-entry';
import {BackendCustomerWithContacts} from './backend-customer-with-contacts';

export interface BackendApplication {
  id: number;
  applicationId: string;
  project: BackendProject;
  handler: BackendUser;
  status: string;
  type: string;
  kind: string;
  metadataVersion: number;
  name: string;
  creationTime: string;
  startTime: string;
  endTime: string;
  recurringEndTime: string;
  customersWithContacts: Array<BackendCustomerWithContacts>;
  locations: Array<BackendLocation>;
  extension: any;
  decisionTime: string;
  decisionMaker: string;
  decisionDistributionType?: string;
  decisionPublicityType?: string;
  decisionDistributionList?: Array<BackendDistributionEntry>;
  attachmentList: Array<BackendAttachmentInfo>;
  calculatedPrice: number;
  priceOverride: number;
  priceOverrideReason: string;
  applicationTags: Array<BackendApplicationTag>;
  comments?: Array<BackendComment>;
}
