import {Component, OnInit} from '@angular/core';
import * as filesaverLib from 'filesaver';

import {AttachmentInfo} from '../../../model/application/attachment/attachment-info';
import {Application} from '../../../model/application/application';
import {MaterializeUtil} from '../../../util/materialize.util';
import {ApplicationState} from '../../../service/application/application-state';
import {AttachmentHub} from './attachment-hub';
import {ConfirmDialogComponent} from '../../common/confirm-dialog/confirm-dialog.component';
import {MdDialog} from '@angular/material';
import {TimeUtil} from '../../../util/time.util';

const toastTime = 4000;

@Component({
  selector: 'attachments',
  template: require('./attachments.component.html'),
  styles: [
    require('./attachments.component.scss')
  ]
})
export class AttachmentsComponent implements OnInit {
  application: Application;
  attachments: AttachmentInfo[] = [];
  editableAttachments: AttachmentInfo[] = [];

  constructor(private attachmentHub: AttachmentHub,
              private applicationState: ApplicationState,
              private dialog: MdDialog) {}

  ngOnInit() {
    this.setApplication(this.applicationState.application);
    this.applicationState.attachments
      .map(attachments => attachments.sort((l, r) => TimeUtil.compareTo(r.creationTime, l.creationTime))) // sort latest first
      .subscribe(attachments => this.attachments = attachments);
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
    let dialogRef = this.dialog.open(ConfirmDialogComponent);
    let component = dialogRef.componentInstance;
    component.title = 'Haluatko varmasti poistaa liitteen';
    component.description = attachment.name;
    dialogRef.afterClosed().subscribe(result => this.onRemoveConfirm(index, attachment, result));
  }

  cancel(index: number): void {
    this.editableAttachments.splice(index, 1);
  }

  download(attachment: AttachmentInfo) {
    this.attachmentHub.download(attachment.id, attachment.name)
      .subscribe(file => filesaverLib.saveAs(file));
  }

  private onRemoveConfirm(index: number, attachment: AttachmentInfo, result: boolean) {
    if (result) {
      this.applicationState.removeAttachment(index, attachment.id)
        .subscribe(status => {
            MaterializeUtil.toast('Liite ' + attachment.name + ' poistettu', toastTime);
          },
          error => MaterializeUtil.toast('Liiteen ' + attachment.name + ' poistaminen epäonnistui', toastTime));
    }
  }

  private setApplication(app: Application) {
    this.application = app;
    // Only new applications can have pending attachments
    if (!app.id) {
      this.attachments = this.applicationState.pendingAttachments;
    }
  }
}
