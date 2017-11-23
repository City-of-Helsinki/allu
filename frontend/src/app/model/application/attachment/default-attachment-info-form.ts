import {AttachmentInfoForm} from './attachment-info-form';

export interface DefaultAttachmentInfoForm extends AttachmentInfoForm {
  defaultAttachmentId?: number;
  applicationTypes?: Array<string>;
  fixedLocationId?: number;
}
