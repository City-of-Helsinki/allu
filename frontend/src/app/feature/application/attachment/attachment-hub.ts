import {Injectable} from '@angular/core';

import {AttachmentService} from '../../../service/attachment-service';
import {AttachmentInfo} from '../../../model/application/attachment/attachment-info';
import {DefaultAttachmentInfo} from '../../../model/application/attachment/default-attachment-info';
import {ApplicationType} from '../../../model/application/type/application-type';
import {AttachmentType} from '../../../model/application/attachment/attachment-type';

@Injectable()
export class AttachmentHub {

  constructor(private attachmentService: AttachmentService) {
  }

  /**
   * Uploads given attachments and adds them to given application
   */
  upload = (applicationId: number, attachments: AttachmentInfo[]) =>
    this.attachmentService.uploadFiles(applicationId, attachments);

  /**
   * Removes given attachment
   */
  remove = (applicationId: number, attachmentId: number) => this.attachmentService.remove(applicationId, attachmentId);

  /**
   * Retrieves attachment in downloadable format
   */
  download = (attachmentId: number) => this.attachmentService.download(attachmentId);


  /**
   * Fetches single default attachment info
   */
  defaultAttachmentInfo = (id) => this.attachmentService.getDefaultAttachmentInfo(id);

  /**
   * Fetches all default attachment infos
   */
  defaultAttachmentInfos = () => this.attachmentService.getDefaultAttachmentInfos();

  /**
   * Fetches default attachment infos which are for given application- and attachment type
   */
  defaultAttachmentInfosBy = (applicationType: ApplicationType, attachmentType?: AttachmentType) =>
    this.attachmentService.getDefaultAttachmentInfosByType(applicationType)
      .map(attachments => this.filterByAttachmentType(attachments, attachmentType));

  /**
   * Fetches default attachment infos which are for given application type and area
   */
  defaultAttachmentInfosByArea = (applicationType: ApplicationType, areaId: number) =>
    this.defaultAttachmentInfosBy(applicationType)
      .map(attachments => attachments.filter((a: DefaultAttachmentInfo) => a.fixedLocationId === areaId));

  /**
   * Saves given default attachment
   */
  saveDefaultAttachments = (attachment: DefaultAttachmentInfo) => this.attachmentService.saveDefaultAttachments(attachment);

  /**
   * Removes given default attachment by id
   */
  removeDefaultAttachment = (id: number) => this.attachmentService.removeDefaultAttachment(id);

  private filterByAttachmentType(attachments: Array<AttachmentInfo>, attachmentType?: AttachmentType) {
    return attachmentType === undefined
      ? attachments
      : attachments.filter(a => AttachmentType[a.type] === attachmentType);
  }
}
