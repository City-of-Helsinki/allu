import {Component, Input, Output, OnInit, EventEmitter} from '@angular/core';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';

import {AttachmentInfo} from '../../../model/application/attachment/attachment-info';
import {AttachmentType} from '../../../model/application/attachment/attachment-type';

@Component({
  selector: 'attachment',
  template: require('./attachment.component.html'),
  styles: []
})
export class AttachmentComponent implements OnInit {
  @Input() attachment: AttachmentInfo = new AttachmentInfo();
  @Output() onCancel = new EventEmitter<void>();
  @Output() onSave = new EventEmitter<AttachmentInfo>();

  attachmentForm: FormGroup;
  attachmentTypes = [
    AttachmentType[AttachmentType.ADDED_BY_CUSTOMER],
    AttachmentType[AttachmentType.ADDED_BY_HANDLER]
  ];

  constructor(fb: FormBuilder) {
    this.attachmentForm = fb.group({
      id: [],
      type: ['', Validators.required],
      name:  ['', Validators.required],
      description: [],
      size: [],
      creationTime: [],
      handlerName: [],
      file: []
    });
  }

  ngOnInit(): void {
    this.attachmentForm.patchValue(AttachmentInfo.toForm(this.attachment));

    if (!!this.attachment.id) {
      this.attachmentForm.disable();
    }
  }

  save(): void {
    let form = this.attachmentForm.value;
    let attachment = AttachmentInfo.fromForm(form);
    this.onSave.emit(attachment);
  }

  cancel(): void {
    this.onCancel.emit();
  }

  attachmentSelected(files: any[]): void {
    if (files && files.length > 0) {
      let file = files[0];
      this.attachmentForm.patchValue({name: file.name, file: file});
    }
  }
}
