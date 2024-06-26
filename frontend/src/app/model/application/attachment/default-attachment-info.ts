import {AttachmentInfo} from './attachment-info';
import {TimeUtil} from '../../../util/time.util';
import {DefaultAttachmentInfoForm} from './default-attachment-info-form';
import {AttachmentType} from '@model/application/attachment/attachment-type';

export class DefaultAttachmentInfo extends AttachmentInfo {
  constructor(
    public id?: number,
    public type?: AttachmentType,
    public mimeType?: string,
    public name?: string,
    public description?: string,
    public size?: number,
    public creationTime?: Date,
    public decisionAttachment?: boolean,
    public handlerName?: string,
    public file?:  Blob | File,
    public defaultAttachmentId?: number,
    public applicationTypes?: Array<string>,
    public fixedLocationId?: number
  ) {
    super(id, type, mimeType, name, description, size, creationTime, decisionAttachment, handlerName, file);
  }

  static fromForm(form: DefaultAttachmentInfoForm): DefaultAttachmentInfo {
    return new DefaultAttachmentInfo(
      form.id,
      form.type,
      form.mimeType,
      form.name,
      form.description,
      form.size,
      TimeUtil.getDateFromUi(form.creationTime),
      true,
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
      mimeType: attachmentInfo.mimeType,
      name: attachmentInfo.name,
      description: attachmentInfo.description,
      creationTime: attachmentInfo.uiCreationTime,
      decisionAttachment: attachmentInfo.decisionAttachment,
      handlerName: attachmentInfo.handlerName,
      file: attachmentInfo.file,
      defaultAttachmentId: attachmentInfo.defaultAttachmentId,
      applicationTypes: attachmentInfo.applicationTypes,
      fixedLocationId: attachmentInfo.fixedLocationId
    };
  }
}

