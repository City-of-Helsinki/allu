import {AttachmentType} from '@model/application/attachment/attachment-type';

export interface AttachmentInfoForm {
  id?: number;
  type?: AttachmentType;
  mimeType?: string;
  name?: string;
  description?: string;
  size?: number;
  creationTime?: string;
  decisionAttachment?: boolean;
  handlerName?: string;
  file?: Blob | File;
}
