import {Component, OnInit, AfterViewInit} from '@angular/core';
import * as filesaverLib from 'filesaver';

import {AttachmentInfo} from '../../../model/application/attachment/attachment-info';
import {Application} from '../../../model/application/application';
import {MaterializeUtil} from '../../../util/materialize.util';
import {ApplicationState} from '../../../service/application/application-state';
import {AttachmentHub} from './attachment-hub';

const toastTime = 4000;
const URL = '/api/applications/appId/attachments';

@Component({
  selector: 'attachments',
  template: require('./attachments.component.html'),
  styles: [
    require('./attachments.component.scss')
  ]
})
export class AttachmentsComponent implements OnInit, AfterViewInit {
  application: Application;
  attachments: AttachmentInfo[] = [];
  editableAttachments: AttachmentInfo[] = [];

  constructor(private attachmentHub: AttachmentHub,
              private applicationState: ApplicationState) {}

  ngOnInit() {
    this.setApplication(this.applicationState.application);
  }

  ngAfterViewInit(): void {
    MaterializeUtil.updateTextFields(50);
  }

  addNewAttachment(attachment?: AttachmentInfo): void {
    let att = attachment || new AttachmentInfo();
    att.creationTime = new Date();
    this.editableAttachments.push(att);
  }

  onFileDrop(fileList: FileList) {
    for (let i = 0; i < fileList.length; ++i) {
      let file = fileList.item(i);
      this.addNewAttachment(AttachmentInfo.fromFile(file));
    }
  }

  save(index, attachment: AttachmentInfo) {
    if (this.application.id) {
      this.applicationState.saveAttachment(this.application.id, attachment).subscribe(
        saved => {
          MaterializeUtil.toast('Liite ' + saved.name + ' tallennettu', toastTime);
          this.attachments.push(saved);
          this.editableAttachments.splice(index, 1);
        },
        error => MaterializeUtil.toast('Liiteen ' + attachment.name + ' tallennus epäonnistui', toastTime)
      );
    } else {
      this.applicationState.addAttachment(attachment);
      this.editableAttachments.splice(index, 1);
      MaterializeUtil.toast('Liite ' + attachment.name + ' lisätty hakemukselle', toastTime);
    }

  }

  remove(index: number, attachment: AttachmentInfo) {
    this.applicationState.removeAttachment(index, attachment.id)
      .subscribe(status => {
          MaterializeUtil.toast('Liite ' + attachment.name + ' poistettu', toastTime);
          this.attachments.splice(index, 1);
        },
        error => MaterializeUtil.toast('Liiteen ' + attachment.name + ' poistaminen epäonnistui', toastTime));
  }

  cancel(index: number): void {
    this.editableAttachments.splice(index, 1);
  }

  download(attachment: AttachmentInfo) {
    this.attachmentHub.download(attachment.id, attachment.name)
      .subscribe(file => filesaverLib.saveAs(file));
  }

  private setApplication(app: Application) {
    this.application = app;
    // Only new applications can have pending attachments
    this.attachments = app.id ? app.attachmentList : this.applicationState.pendingAttachments;
  }
}
