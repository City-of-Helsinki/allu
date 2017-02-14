import {AttachmentInfo} from './attachment-info';
import {TimeUtil} from '../../../util/time.util';

export class DefaultAttachmentInfo extends AttachmentInfo {
  constructor(
    public id?: number,
    public type?: string,
    public name?: string,
    public description?: string,
    public size?: number,
    public creationTime?: Date,
    public handlerName?: string,
    public file?: any,
    public defaultAttachmentId?: number,
    public applicationTypes?: Array<string>,
    public fixedLocationId?: number
  ) {
    super(id, type, name, description, size, creationTime, handlerName, file);
  };

  static fromForm(form: DefaultAttachmentInfoForm, type: string): DefaultAttachmentInfo {
    return new DefaultAttachmentInfo(
      form.id,
      type,
      form.name,
      form.description,
      form.size,
      TimeUtil.getDateFromUi(form.creationTime),
      form.handlerName,
      undefined,
      form.defaultAttachmentId,
      form.applicationTypes,
      form.fixedLocationId
    );
  }

  static toForm(attachmentInfo: DefaultAttachmentInfo): DefaultAttachmentInfoForm {
    return {
      id: attachmentInfo.id,
      name: attachmentInfo.name,
      description: attachmentInfo.description,
      creationTime: attachmentInfo.uiCreationTime,
      handlerName: attachmentInfo.handlerName,
      defaultAttachmentId: attachmentInfo.defaultAttachmentId,
      applicationTypes: attachmentInfo.applicationTypes,
      fixedLocationId: attachmentInfo.fixedLocationId
    };
  }
}

export interface DefaultAttachmentInfoForm {
  id?: number;
  name?: string;
  description?: string;
  size?: number;
  creationTime?: string;
  handlerName?: string;
  defaultAttachmentId?: number;
  applicationTypes?: Array<string>;
  fixedLocationId?: number;
}
