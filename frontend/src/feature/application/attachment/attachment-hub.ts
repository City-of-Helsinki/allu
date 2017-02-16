import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import '../../../rxjs-extensions.ts';

import {AttachmentService} from '../../../service/attachment-service';
import {AttachmentInfo} from '../../../model/application/attachment/attachment-info';
import {DefaultAttachmentInfo} from '../../../model/application/attachment/default-attachment-info';
import {ApplicationType} from '../../../model/application/type/application-type';

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
   * and converts to file with given filename
   */
  download = (attachmentId: number, name: string) => this.attachmentService.download(attachmentId, name);


  /**
   * Fetches single default attachment info
   */
  defaultAttachmentInfo = (id) => this.attachmentService.getDefaultAttachmentInfo(id);

  /**
   * Fetches all default attachment infos
   */
  defaultAttachmentInfos = () => this.attachmentService.getDefaultAttachmentInfos();

  /**
   * Fetches default attachment infos which are for given application type
   */
  defaultAttachmentInfosByType = (applicationType: ApplicationType) =>
    this.attachmentService.getDefaultAttachmentInfosByType(applicationType);

  /**
   * Saves given default attachment
   */
  saveDefaultAttachments = (attachment: DefaultAttachmentInfo) => this.attachmentService.saveDefaultAttachments(attachment);

  /**
   * Removes given default attachment by id
   */
  removeDefaultAttachment = (id: number) => this.attachmentService.removeDefaultAttachment(id);
}
