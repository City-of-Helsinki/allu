import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import * as filesaver from 'file-saver';

import {AttachmentHub} from './attachment-hub';
import {DefaultAttachmentInfo} from '../../../model/application/attachment/default-attachment-info';
import {SelectionEvent} from '../../common/selection-group/selection-event.service';
import {ApplicationType} from '../../../model/application/type/application-type';
import {AttachmentType} from '../../../model/application/attachment/attachment-type';
import {ArrayUtil} from '../../../util/array-util';
import {CurrentUser} from '../../../service/user/current-user';

@Component({
  selector: 'default-attachments',
  templateUrl: './default-attachments.component.html',
  styleUrls: []
})
export class DefaultAttachmentsComponent implements OnInit {
  @Input() applicationType: string;
  @Input() attachmentType: string;
  @Input() selectedAttachments: Array<DefaultAttachmentInfo> = [];
  @Output() add = new EventEmitter<DefaultAttachmentInfo>();
  @Output() remove = new EventEmitter<DefaultAttachmentInfo>();

  defaultAttachments: Array<DefaultAttachmentInfo> = [];
  isAllowedToEdit = false;

  constructor(private attachmentHub: AttachmentHub, private currentUser: CurrentUser) {}

  ngOnInit(): void {
    this.attachmentHub.defaultAttachmentInfosBy(ApplicationType[this.applicationType], AttachmentType[this.attachmentType])
      .map(das => das.sort(ArrayUtil.naturalSort((item: DefaultAttachmentInfo) => item.name)))
      .subscribe(das => this.defaultAttachments = das);

    this.currentUser.hasRole(['ROLE_CREATE_APPLICATION', 'ROLE_PROCESS_APPLICATION'])
      .subscribe(hasValidRole => this.isAllowedToEdit = hasValidRole);
  }

  onSelect(event: SelectionEvent): void {
    const da = event.item;
    da.file = new Blob(['empty']);

    if (event.selected) {
      this.add.emit(da);
    } else {
      this.remove.emit(da);
    }
  }

  download(attachment: DefaultAttachmentInfo) {
    this.attachmentHub.download(attachment.id)
      .subscribe(file => filesaver.saveAs(file, attachment.name));
  }
}
