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
import {Some} from '../../../util/option';
import {isCommon} from '../../../model/application/attachment/attachment-type';

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
  commonAttachments: AttachmentInfo[] = [];
  defaultAttachments: AttachmentInfo[] = [];
  editableAttachments: AttachmentInfo[] = [];

  constructor(private attachmentHub: AttachmentHub,
              private applicationState: ApplicationState,
              private dialog: MdDialog) {}

  ngOnInit() {
    this.setApplication(this.applicationState.application);
    this.applicationState.attachments
      .map(attachments => attachments.sort((l, r) => TimeUtil.compareTo(r.creationTime, l.creationTime))) // sort latest first
      .subscribe(attachments => this.setAttachments(attachments));
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

  save(attachment: AttachmentInfo, index?: number) {
    if (this.application.id) {
      this.applicationState.saveAttachment(this.application.id, attachment).subscribe(
        saved => {
          MaterializeUtil.toast('Liite ' + saved.name + ' tallennettu', toastTime);
          Some(index).do(i => this.editableAttachments.splice(i, 1));
        },
        error => MaterializeUtil.toast('Liiteen ' + attachment.name + ' tallennus epäonnistui', toastTime)
      );
    } else {
      this.applicationState.addAttachment(attachment);
      Some(index).do(i => this.editableAttachments.splice(i, 1));
      MaterializeUtil.toast('Liite ' + attachment.name + ' lisätty hakemukselle', toastTime);
    }
  }

  remove(attachment: AttachmentInfo, index?: number) {
    if (isCommon(attachment.type)) {
      let dialogRef = this.dialog.open(ConfirmDialogComponent);
      let component = dialogRef.componentInstance;
      component.title = 'Haluatko varmasti poistaa liitteen';
      component.description = attachment.name;
      dialogRef.afterClosed()
        .filter(result => result) // Ignore no answers
        .subscribe(result => this.onRemoveConfirm(attachment, index));
    } else {
      this.onRemoveConfirm(attachment, index);
    }
  }

  cancel(index: number): void {
    this.editableAttachments.splice(index, 1);
  }

  download(attachment: AttachmentInfo) {
    this.attachmentHub.download(attachment.id, attachment.name)
      .subscribe(file => filesaverLib.saveAs(file));
  }

  private onRemoveConfirm(attachment: AttachmentInfo, index?: number) {
    this.applicationState.removeAttachment(attachment.id, index)
      .subscribe(status => {
          MaterializeUtil.toast('Liite ' + attachment.name + ' poistettu', toastTime);
        },
        error => MaterializeUtil.toast('Liiteen ' + attachment.name + ' poistaminen epäonnistui', toastTime));

  }

  private setApplication(app: Application) {
    this.application = app;
    // Only new applications can have pending attachments
    if (!app.id) {
      this.setAttachments(this.applicationState.pendingAttachments);
    }
  }

  private setAttachments(attachments: Array<AttachmentInfo>): void {
    this.commonAttachments = attachments.filter(a => isCommon(a.type));
    this.defaultAttachments = attachments.filter(a => !isCommon(a.type));
  }
}
