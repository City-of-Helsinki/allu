import {BackendAttachmentInfo} from './backend-attachment-info';

export interface BackendDefaultAttachmentInfo extends BackendAttachmentInfo {
  defaultAttachmentId: number;
  applicationTypes: Array<string>;
  fixedLocationId: number;
}
