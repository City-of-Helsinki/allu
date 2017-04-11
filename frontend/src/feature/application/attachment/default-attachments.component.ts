import {Component, Output, OnInit, EventEmitter, Input} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import * as filesaverLib from 'filesaver';

import {AttachmentHub} from './attachment-hub';
import {DefaultAttachmentInfo} from '../../../model/application/attachment/default-attachment-info';
import {SelectionEvent} from '../../common/selection-group/selection-event.service';
import {ApplicationType} from '../../../model/application/type/application-type';
import {AttachmentType} from '../../../model/application/attachment/attachment-type';
import {ArrayUtil} from '../../../util/array-util';

@Component({
  selector: 'default-attachments',
  template: require('./default-attachments.component.html'),
  styles: []
})
export class DefaultAttachmentsComponent implements OnInit {
  @Input() applicationType: string;
  @Input() attachmentType: string;
  @Input() selectedAttachments: Array<DefaultAttachmentInfo> = [];
  @Output() add = new EventEmitter<DefaultAttachmentInfo>();
  @Output() remove = new EventEmitter<DefaultAttachmentInfo>();
  defaultAttachments: Array<DefaultAttachmentInfo> = [];

  constructor(private attachmentHub: AttachmentHub) {}

  ngOnInit(): void {
    this.attachmentHub.defaultAttachmentInfosBy(ApplicationType[this.applicationType], AttachmentType[this.attachmentType])
      .map(das => das.sort(ArrayUtil.naturalSort((item: DefaultAttachmentInfo) => item.name)))
      .subscribe(das => this.defaultAttachments = das);
  }

  onSelect(event: SelectionEvent): void {
    let da = event.item;
    da.file = new Blob(['empty']);

    if (event.selected) {
      this.add.emit(da);
    } else {
      this.remove.emit(da);
    }
  }

  download(attachment: DefaultAttachmentInfo) {
    this.attachmentHub.download(attachment.id)
      .subscribe(file => filesaverLib.saveAs(file, attachment.name));
  }
}
