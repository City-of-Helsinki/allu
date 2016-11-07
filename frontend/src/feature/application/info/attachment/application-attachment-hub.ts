import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import '../../../../rxjs-extensions.ts';

import {AttachmentService} from '../../../../service/attachment-service';
import {AttachmentInfo} from '../../../../model/application/attachment-info';

@Injectable()
export class ApplicationAttachmentHub {

  constructor(private attachmentService: AttachmentService) {
  }

  public upload(applicationId: number, attachments: AttachmentInfo[]): Observable<number> {
    return this.attachmentService.uploadFiles(applicationId, attachments);
  }
}
