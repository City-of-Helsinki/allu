import {TimeUtil} from '../../../util/time.util';
import {AttachmentInfoForm} from './attachment-info-form';
import {AttachmentType} from '@model/application/attachment/attachment-type';

export class AttachmentInfo {
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
    public file?:  Blob | File
  ) {}

  get uiCreationTime(): string {
    return TimeUtil.getUiDateTimeString(this.creationTime);
  }

  static fromFile(file: File): AttachmentInfo {
    const attachment = new AttachmentInfo();
    attachment.name = file.name;
    attachment.file = file;
    return attachment;
  }

  static fromForm(form: AttachmentInfoForm): AttachmentInfo {
    return new AttachmentInfo(
      form.id,
      form.type,
      form.mimeType,
      form.name,
      form.description,
      form.size,
      TimeUtil.getDateFromUi(form.creationTime),
      form.decisionAttachment,
      form.handlerName,
      form.file
    );
  }

  static toForm(attachmentInfo: AttachmentInfo): AttachmentInfoForm {
    return {
      id: attachmentInfo.id,
      type: attachmentInfo.type,
      mimeType: attachmentInfo.mimeType,
      name: attachmentInfo.name,
      description: attachmentInfo.description,
      creationTime: attachmentInfo.uiCreationTime,
      decisionAttachment: attachmentInfo.decisionAttachment,
      handlerName: attachmentInfo.handlerName,
      file: attachmentInfo.file
    };
  }
}

