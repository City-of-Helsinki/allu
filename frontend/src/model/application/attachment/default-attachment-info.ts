import {AttachmentInfo} from './attachment-info';
import {TimeUtil} from '../../../util/time.util';
import {DefaultAttachmentInfoForm} from './default-attachment-info-form';

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

  static fromForm(form: DefaultAttachmentInfoForm): DefaultAttachmentInfo {
    return new DefaultAttachmentInfo(
      form.id,
      form.type,
      form.name,
      form.description,
      form.size,
      TimeUtil.getDateFromUi(form.creationTime),
      form.handlerName,
      form.file,
      form.defaultAttachmentId,
      form.applicationTypes,
      form.fixedLocationId
    );
  }

  static toForm(attachmentInfo: DefaultAttachmentInfo): DefaultAttachmentInfoForm {
    return {
      id: attachmentInfo.id,
      type: attachmentInfo.type,
      name: attachmentInfo.name,
      description: attachmentInfo.description,
      creationTime: attachmentInfo.uiCreationTime,
      handlerName: attachmentInfo.handlerName,
      file: attachmentInfo.file,
      defaultAttachmentId: attachmentInfo.defaultAttachmentId,
      applicationTypes: attachmentInfo.applicationTypes,
      fixedLocationId: attachmentInfo.fixedLocationId
    };
  }
}

