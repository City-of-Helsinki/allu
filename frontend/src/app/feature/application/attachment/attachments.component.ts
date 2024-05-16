import {Component, OnDestroy, OnInit} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import * as filesaver from 'file-saver';

import {AttachmentInfo} from '@model/application/attachment/attachment-info';
import {Application} from '@model/application/application';
import {ApplicationStore} from '@service/application/application-store';
import {AttachmentHub} from './attachment-hub';
import {ConfirmDialogComponent} from '@feature/common/confirm-dialog/confirm-dialog.component';
import {MatLegacyDialog as MatDialog} from '@angular/material/legacy-dialog';
import {MatLegacySlideToggleChange as MatSlideToggleChange} from '@angular/material/legacy-slide-toggle';
import {TimeUtil} from '@util/time.util';
import {Some} from '@util/option';
import {AttachmentType, isCommon} from '@model/application/attachment/attachment-type';
import {applicationCanBeEdited} from '@model/application/application-status';
import {NotificationService} from '@feature/notification/notification.service';
import {findTranslation} from '@util/translations';
import {CanComponentDeactivate} from '@service/common/can-deactivate-guard';
import {filter, map, takeUntil} from 'rxjs/internal/operators';
import {validForDecision} from '@model/common/file-type';

@Component({
  selector: 'attachments',
  templateUrl: './attachments.component.html',
  styleUrls: [
    './attachments.component.scss'
  ]
})
export class AttachmentsComponent implements OnInit, OnDestroy, CanComponentDeactivate {
  application: Application;
  commonAttachments: AttachmentInfo[] = [];
  defaultAttachments: AttachmentInfo[] = [];
  defaultImages: AttachmentInfo[] = [];
  editableAttachments: AttachmentInfo[] = [];
  hasFileOverDropzone = false;
  applicationCanBeEdited = true;
  isValidForDecision = validForDecision;

  private destroy = new Subject<boolean>();

  constructor(private attachmentHub: AttachmentHub,
              private applicationStore: ApplicationStore,
              private dialog: MatDialog,
              private notification: NotificationService) {}

  ngOnInit() {
    this.application = this.applicationStore.snapshot.application;
    this.applicationCanBeEdited = applicationCanBeEdited(this.application);
    this.applicationStore.attachments.pipe(
      takeUntil(this.destroy),
      map(attachments => attachments.sort((l, r) => TimeUtil.compareTo(r.creationTime, l.creationTime))) // sort latest first
    ).subscribe(sorted => this.setAttachments(sorted));
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
        this.notification.success(findTranslation('attachment.action.added', {name: saved.name}));
        Some(index).do(i => this.editableAttachments.splice(i, 1));
      },
      error => this.notification.error(findTranslation('attachment.error.addFailed', {name: attachment.name}))
    );
  }

  remove(attachment: AttachmentInfo) {
    if (isCommon(attachment.type)) {
      const dialogRef = this.dialog.open(ConfirmDialogComponent, {
        data: {title: findTranslation('attachment.action.confirmDelete'), description: attachment.name}
      });
      dialogRef.afterClosed()
        .pipe(filter(result => result)) // Ignore no answers
        .subscribe(() => this.onRemoveConfirm(attachment));
    } else {
      this.onRemoveConfirm(attachment);
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

  decisionAttachmentToggle(attachment: AttachmentInfo, change: MatSlideToggleChange): void {
    attachment.decisionAttachment = change.checked;
    this.applicationStore.saveAttachment(attachment).subscribe(
      saved => {},
      error => this.notification.error(findTranslation('attachment.error.addFailed', {name: attachment.name}))
    );
  }

  canDeactivate(): Observable<boolean> | boolean {
    if (this.editableAttachments.length > 0) {
      return this.confirmChanges();
    }
    return true;
  }

  private confirmChanges(): Observable<boolean> {
    const data = {
      title: findTranslation(['attachment.confirmDiscard.title']),
      description: findTranslation(['attachment.confirmDiscard.description']),
      confirmText: findTranslation(['attachment.confirmDiscard.confirmText']),
      cancelText: findTranslation(['attachment.confirmDiscard.cancelText'])
    };
    return this.dialog.open(ConfirmDialogComponent, {data}).afterClosed();
  }

  private onRemoveConfirm(attachment: AttachmentInfo) {
    this.applicationStore.removeAttachment(attachment.id)
      .subscribe(
        status => this.notification.success(findTranslation('attachment.action.deleted', {name: attachment.name})),
        error => this.notification.error(findTranslation('attachment.error.deleteFailed', {name: attachment.name})));
  }

  private setAttachments(attachments: Array<AttachmentInfo>): void {
    this.commonAttachments = attachments.filter(a => isCommon(a.type));
    this.defaultAttachments = attachments.filter(a => a.type === AttachmentType.DEFAULT);
    this.defaultImages = attachments.filter(a => a.type === AttachmentType.DEFAULT_IMAGE);
  }
}
