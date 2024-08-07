import {Injectable} from '@angular/core';

import {AttachmentService} from '../../../service/attachment-service';
import {AttachmentInfo} from '../../../model/application/attachment/attachment-info';
import {DefaultAttachmentInfo} from '../../../model/application/attachment/default-attachment-info';
import {ApplicationType} from '../../../model/application/type/application-type';
import {AttachmentType} from '../../../model/application/attachment/attachment-type';
import {Observable} from 'rxjs';
import {map} from 'rxjs/internal/operators';

@Injectable()
export class AttachmentHub {

  constructor(private attachmentService: AttachmentService) {
  }

  /**
   * Uploads given attachments and adds them to given application
   */
  upload = (applicationId: number, attachments: AttachmentInfo[]) =>
    this.attachmentService.uploadFiles(applicationId, attachments)

  /**
   * Removes given attachment
   */
  remove = (applicationId: number, attachmentId: number) => this.attachmentService.remove(applicationId, attachmentId);

  /**
   * Retrieves attachment in downloadable format
   */
  download = (attachmentId: number) => this.attachmentService.download(attachmentId);


  /**
   * Updates attachment info
   */
  update = (attachment: AttachmentInfo) => this.attachmentService.updateAttachmentInfo(attachment);

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
    this.attachmentService.getDefaultAttachmentInfosByType(applicationType).pipe(
      map(attachments => this.filterByAttachmentType(attachments, attachmentType))
    )

  /**
   * Fetches default attachment infos which are for given application type and area
   */
  defaultAttachmentInfosByArea = (applicationType: ApplicationType, fixedLocationIds: number[]) =>
    this.defaultAttachmentInfosBy(applicationType).pipe(
      map(attachments => attachments.filter((a: DefaultAttachmentInfo) => fixedLocationIds.indexOf(a.fixedLocationId) >= 0))
    )

  /**
   * Saves given default attachment
   */
  saveDefaultAttachment = (attachment: DefaultAttachmentInfo) => this.attachmentService.saveDefaultAttachment(attachment);

  /**
   * Removes given default attachment by id
   */
  removeDefaultAttachment = (id: number) => this.attachmentService.removeDefaultAttachment(id);

  private filterByAttachmentType(attachments: Array<AttachmentInfo>, attachmentType?: AttachmentType) {
    return attachmentType === undefined
      ? attachments
      : attachments.filter(a => a.type === attachmentType);
  }
}
