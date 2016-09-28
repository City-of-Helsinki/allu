import {Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChange} from '@angular/core';
import {AttachmentService} from '../../../service/attachment-service';
import {AttachmentInfo} from '../../../model/application/attachment-info';

import * as filesaverLib from 'filesaver';

@Component({
  selector: 'application-attachment',
  template: require('./application-attachment.component.html'),
  styles: [
    require('./application-attachment.component.scss')
  ]
})
export class ApplicationAttachmentComponent implements OnChanges {

  @Input() readonly: boolean = false;
  @Input() existingAttachments: AttachmentInfo[] = [];
  @Output() currentAttachments = new EventEmitter();

  private attachments: AttachmentInfo[] = [];
  private attachmentsInitialized = false;

  constructor(private attachmentService: AttachmentService) {}


  ngOnChanges(changes: {[key: string]: SimpleChange}): any {
    if (!this.attachmentsInitialized && this.existingAttachments) {
      // we're doing this only once to initialize the attachment list. Further changes in host component list will not affect this component
      this.attachmentsInitialized = true;
      this.attachments = this.existingAttachments || [];
    }
  }

  public attachmentsSelected(files: any[]): void {
    let toBeUploadedAttachments = files.map(f => new AttachmentInfo(undefined, f.name, undefined, undefined, undefined, f));
    this.attachments = this.attachments.concat(toBeUploadedAttachments);
    this.currentAttachments.emit(this.attachments);
  }

  public removeAttachment(index: number): void {
    // TODO: if the file has been uploded already, it should be removed from the backend too!
    this.attachments.splice(index, 1);
    this.currentAttachments.emit(this.attachments);
  }

  public download(index: number) {

    /**********
     * TODO: replace direct use of XMLHttpRequest with Angular's Http (or AuthHttp) when it supports blobs:
     * https://github.com/angular/angular/pull/10190
     */

    let xhr = new XMLHttpRequest();
    let attachmentInfo = this.attachments[index];
    let url = '/api/applications/attachments/index/data'.replace('index', String(attachmentInfo.id));

    xhr.open('GET', url, true);
    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.getItem('jwt'));
    xhr.responseType = 'blob';

    xhr.onreadystatechange = function () {
      // If we get an HTTP status OK (200), save the file using fileSaver
      if (xhr.readyState === 4 && xhr.status === 200) {
        let blob = new Blob([this.response], {type: 'application/pdf'});
        filesaverLib.saveAs(blob, attachmentInfo.name);
      }
    };

    xhr.send();
  }
}
