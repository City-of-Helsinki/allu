import {Component, OnDestroy, OnInit} from '@angular/core';
import * as filesaver from 'file-saver';

import {AttachmentInfo} from '../../../model/application/attachment/attachment-info';
import {Application} from '../../../model/application/application';
import {ApplicationStore} from '../../../service/application/application-store';
import {AttachmentHub} from './attachment-hub';
import {ConfirmDialogComponent} from '../../common/confirm-dialog/confirm-dialog.component';
import {MatDialog, MatSlideToggleChange} from '@angular/material';
import {TimeUtil} from '../../../util/time.util';
import {Some} from '../../../util/option';
import {AttachmentType, isCommon} from '../../../model/application/attachment/attachment-type';
import {Subject} from 'rxjs/Subject';
import {applicationCanBeEdited} from '../../../model/application/application-status';
import {NumberUtil} from '../../../util/number.util';
import {NotificationService} from '../../../service/notification/notification.service';
import {findTranslation} from '../../../util/translations';

const toastTime = 4000;

@Component({
  selector: 'attachments',
  templateUrl: './attachments.component.html',
  styleUrls: [
    './attachments.component.scss'
  ]
})
export class AttachmentsComponent implements OnInit, OnDestroy {
  application: Application;
  commonAttachments: AttachmentInfo[] = [];
  defaultAttachments: AttachmentInfo[] = [];
  defaultImages: AttachmentInfo[] = [];
  editableAttachments: AttachmentInfo[] = [];
  hasFileOverDropzone = false;
  applicationCanBeEdited = true;

  private destroy = new Subject<boolean>();

  constructor(private attachmentHub: AttachmentHub,
              private applicationStore: ApplicationStore,
              private dialog: MatDialog) {}

  ngOnInit() {
    this.application = this.applicationStore.snapshot.application;
    this.applicationCanBeEdited = applicationCanBeEdited(this.application.statusEnum);
    this.applicationStore.allAttachments
      .takeUntil(this.destroy)
      .map(attachments => attachments.sort((l, r) => TimeUtil.compareTo(r.creationTime, l.creationTime))) // sort latest first
      .subscribe(sorted => this.setAttachments(sorted));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
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
    this.applicationStore.saveAttachment(attachment).subscribe(
      saved => {
        NotificationService.message(findTranslation('attachment.action.added', {name: saved.name}), toastTime);
        Some(index).do(i => this.editableAttachments.splice(i, 1));
      },
      error => NotificationService.errorMessage(findTranslation('attachment.error.addFailed', {name: attachment.name}), toastTime)
    );
  }

  remove(attachment: AttachmentInfo, index?: number) {
    if (isCommon(attachment.type)) {
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        data: {title: findTranslation('attachment.action.confirmDelete'), description: attachment.name}
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

  canEdit(attachment: AttachmentInfo): boolean {
    return !attachment.decisionAttachment || this.applicationCanBeEdited;
  }

  download(attachment: AttachmentInfo) {
    this.attachmentHub.download(attachment.id)
      .subscribe(file => filesaver.saveAs(file, attachment.name));
  }

  fileOverDropzone(hasFileOverDropzone: boolean) {
    this.hasFileOverDropzone = hasFileOverDropzone;
  }

  decisionAttachmentToggle(attachment: AttachmentInfo, index: number, change: MatSlideToggleChange): void {
    attachment.decisionAttachment = change.checked;
    this.applicationStore.saveAttachment(attachment, index).subscribe(
      saved => {},
      error => NotificationService.errorMessage(findTranslation('attachment.error.addFailed', {name: attachment.name}), toastTime)
    );
  }

  private onRemoveConfirm(attachment: AttachmentInfo, index?: number) {
    this.applicationStore.removeAttachment(attachment.id, index)
      .subscribe(
        status => NotificationService.message(findTranslation('attachment.action.deleted', {name: attachment.name}), toastTime),
        error => NotificationService.errorMessage(findTranslation('attachment.error.deleteFailed', {name: attachment.name}), toastTime));

  }

  private setAttachments(attachments: Array<AttachmentInfo>): void {
    this.commonAttachments = attachments.filter(a => isCommon(a.type));
    this.defaultAttachments = attachments.filter(a => AttachmentType[a.type] === AttachmentType.DEFAULT);
    this.defaultImages = attachments.filter(a => AttachmentType[a.type] === AttachmentType.DEFAULT_IMAGE);
  }
}
