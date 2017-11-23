import {Component, OnInit} from '@angular/core';
import * as filesaver from 'file-saver';

import {AttachmentInfo} from '../../../model/application/attachment/attachment-info';
import {Application} from '../../../model/application/application';
import {MaterializeUtil} from '../../../util/materialize.util';
import {ApplicationState} from '../../../service/application/application-state';
import {AttachmentHub} from './attachment-hub';
import {ConfirmDialogComponent} from '../../common/confirm-dialog/confirm-dialog.component';
import {MatDialog} from '@angular/material';
import {TimeUtil} from '../../../util/time.util';
import {Some} from '../../../util/option';
import {AttachmentType, isCommon} from '../../../model/application/attachment/attachment-type';

const toastTime = 4000;

@Component({
  selector: 'attachments',
  templateUrl: './attachments.component.html',
  styleUrls: [
    './attachments.component.scss'
  ]
})
export class AttachmentsComponent implements OnInit {
  application: Application;
  commonAttachments: AttachmentInfo[] = [];
  defaultAttachments: AttachmentInfo[] = [];
  defaultImages: AttachmentInfo[] = [];
  editableAttachments: AttachmentInfo[] = [];
  hasFileOverDropzone = false;

  constructor(private attachmentHub: AttachmentHub,
              private applicationState: ApplicationState,
              private dialog: MatDialog) {}

  ngOnInit() {
    this.application = this.applicationState.application;
    this.applicationState.allAttachments
      .map(attachments => attachments.sort((l, r) => TimeUtil.compareTo(r.creationTime, l.creationTime))) // sort latest first
      .subscribe(sorted => this.setAttachments(sorted));
  }

  addNewAttachment(attachment?: AttachmentInfo): void {
    const att = attachment || new AttachmentInfo();
    att.creationTime = new Date();
    this.editableAttachments.push(att);
  }

  onFileDrop(fileList: FileList) {
    for (let i = 0; i < fileList.length; ++i) {
      const file = fileList.item(i);
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
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        data: {title: 'Haluatko varmasti poistaa liitteen', description: attachment.name}
      });
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
    this.attachmentHub.download(attachment.id)
      .subscribe(file => filesaver.saveAs(file, attachment.name));
  }

  fileOverDropzone(hasFileOverDropzone: boolean) {
    this.hasFileOverDropzone = hasFileOverDropzone;
  }

  private onRemoveConfirm(attachment: AttachmentInfo, index?: number) {
    this.applicationState.removeAttachment(attachment.id, index)
      .subscribe(status => {
          MaterializeUtil.toast('Liite ' + attachment.name + ' poistettu', toastTime);
        },
        error => MaterializeUtil.toast('Liiteen ' + attachment.name + ' poistaminen epäonnistui', toastTime));

  }

  private setAttachments(attachments: Array<AttachmentInfo>): void {
    this.commonAttachments = attachments.filter(a => isCommon(a.type));
    this.defaultAttachments = attachments.filter(a => AttachmentType[a.type] === AttachmentType.DEFAULT);
    this.defaultImages = attachments.filter(a => AttachmentType[a.type] === AttachmentType.DEFAULT_IMAGE);
  }
}
