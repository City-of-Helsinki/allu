import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import '../../../../rxjs-extensions.ts';

import {AttachmentService} from '../../../../service/attachment-service';
import {AttachmentInfo} from '../../../../model/application/attachment/attachment-info';

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
  remove = (attachmentId: number) => this.attachmentService.remove(attachmentId);

  /**
   * Retrieves attachment in downloadable format
   * and converts to file with given filename
   */
  download = (attachmentId: number, name: string) => this.attachmentService.download(attachmentId, name);
}
