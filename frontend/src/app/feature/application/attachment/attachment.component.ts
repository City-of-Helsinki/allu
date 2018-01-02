import {Component, Input, Output, OnInit, EventEmitter} from '@angular/core';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';

import {AttachmentInfo} from '../../../model/application/attachment/attachment-info';
import {AttachmentType} from '../../../model/application/attachment/attachment-type';

@Component({
  selector: 'attachment',
  templateUrl: './attachment.component.html',
  styleUrls: []
})
export class AttachmentComponent implements OnInit {
  @Input() attachment: AttachmentInfo = new AttachmentInfo();
  @Input() decisionAttachmentDisabled = false;
  @Output() onCancel = new EventEmitter<void>();
  @Output() onSave = new EventEmitter<AttachmentInfo>();

  attachmentForm: FormGroup;
  attachmentTypes = [
    AttachmentType[AttachmentType.ADDED_BY_CUSTOMER],
    AttachmentType[AttachmentType.ADDED_BY_HANDLER]
  ];

  constructor(private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.attachmentForm = this.fb.group({
      id: [],
      type: ['', Validators.required],
      name:  ['', Validators.required],
      description: [],
      size: [],
      creationTime: [],
      decisionAttachment: [{value: false, disabled: this.decisionAttachmentDisabled}],
      handlerName: [],
      file: []
    });

    this.attachmentForm.patchValue(AttachmentInfo.toForm(this.attachment));

    if (!!this.attachment.id) {
      this.attachmentForm.disable();
    }
  }

  save(): void {
    const form = this.attachmentForm.value;
    const attachment = AttachmentInfo.fromForm(form);
    attachment.mimeType = form.file.type;
    this.onSave.emit(attachment);
  }

  cancel(): void {
    this.onCancel.emit();
  }

  attachmentSelected(files: any[]): void {
    if (files && files.length > 0) {
      const file = files[0];
      this.attachmentForm.patchValue({name: file.name, file: file});
    }
  }
}
