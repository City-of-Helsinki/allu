import {Component, Input, Output, OnInit, EventEmitter} from '@angular/core';

import {AttachmentInfo} from '../../../../model/application/attachment/attachment-info';
import {EnumUtil} from '../../../../util/enum.util';
import {AttachmentType} from '../../../../model/application/attachment/attachment-type';


@Component({
  selector: 'attachment',
  template: require('./attachment.component.html'),
  styles: [
    require('./attachment.component.scss')
  ]
})
export class AttachmentComponent implements OnInit {
  @Input() applicationId: number;
  @Input() attachment: AttachmentInfo = new AttachmentInfo();
  @Output() onCancel = new EventEmitter<void>();
  @Output() onSave = new EventEmitter<AttachmentInfo>();

  edit = true;
  attachmentTypes = EnumUtil.enumValues(AttachmentType);

  constructor() {}

  ngOnInit(): void {
    this.edit = this.attachment.id === undefined;
  }

  attachmentSelected(files: any[]): void {
    if (files && files.length > 0) {
      let file = files[0];
      this.attachment.name = file.name;
      this.attachment.file = file;
    }
  }

  save(): void {
    this.onSave.emit(this.attachment);
  }

  cancel(): void {
    this.onCancel.emit();
  }
}
