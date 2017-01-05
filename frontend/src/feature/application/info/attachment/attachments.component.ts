import {Component, OnInit, AfterViewInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import * as filesaverLib from 'filesaver';

import {AttachmentInfo} from '../../../../model/application/attachment/attachment-info';
import {Application} from '../../../../model/application/application';
import {MaterializeUtil} from '../../../../util/materialize.util';
import {ApplicationHub} from '../../../../service/application/application-hub';
import {ApplicationState} from '../../../../service/application/application-state';
import {AttachmentHub} from './attachment-hub';

const toastTime = 4000;

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

  constructor(private route: ActivatedRoute,
              private applicationHub: ApplicationHub,
              private attachmentHub: AttachmentHub,
              private applicationState: ApplicationState) {}

  ngOnInit() {
    this.route.data
      .map((data: {application: Application}) => data.application)
      .subscribe(application => this.setApplication(application));
  }

  ngAfterViewInit(): void {
    MaterializeUtil.updateTextFields(50);
  }

  addNewAttachment(): void {
    this.editableAttachments.push(new AttachmentInfo());
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
      MaterializeUtil.toast('Liite ' + attachment.name + ' lisätty hakemukselle', toastTime);
    }
  }

  remove(index: number, attachment: AttachmentInfo) {
    this.applicationState.removeAttachment(attachment.id)
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

  refreshAttachments(): void {
    this.applicationHub.getApplication(this.application.id)
      .subscribe(app => this.setApplication(app));
  }

  private setApplication(app: Application) {
    this.application = app;
    this.attachments = app.attachmentList || [];
  }
}
