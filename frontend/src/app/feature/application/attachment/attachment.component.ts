import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';

import {AttachmentInfo} from '@model/application/attachment/attachment-info';
import {commonAttachmentTypes} from '@model/application/attachment/attachment-type';
import {validForDecision} from '@model/common/file-type';
import { Store, select } from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromConfiguration from '@feature/admin/configuration/reducers';
import { ConfigurationKey } from '@app/model/config/configuration-key';
import { takeUntil, tap } from 'rxjs/operators';
import { Subject } from 'rxjs';

const MB = 1000 * 1000;

@Component({
  selector: 'attachment',
  templateUrl: './attachment.component.html',
  styleUrls: []
})
export class AttachmentComponent implements OnInit, OnDestroy {
  @Input() attachment: AttachmentInfo = new AttachmentInfo();
  @Input() decisionAttachmentDisabled = false;
  @Output() onCancel = new EventEmitter<void>();
  @Output() onSave = new EventEmitter<AttachmentInfo>();

  private destroy = new Subject<boolean>();

  attachmentForm: FormGroup;
  attachmentTypes = commonAttachmentTypes;

  validForDecision = false;

  attachmentAllowedTypes$ = this.store.pipe(
    takeUntil(this.destroy),
    select(fromConfiguration.getConfiguration(ConfigurationKey.ATTACHMENT_ALLOWED_TYPES)),
    tap((configuration) => this.attachmentAllowedTypes = configuration[0]?.value)
  ).subscribe();
  attachmentAllowedTypes: string;

  attachmentMaxSizeMB$ = this.store.pipe(
    takeUntil(this.destroy),
    select(fromConfiguration.getConfiguration(ConfigurationKey.ATTACHMENT_MAX_SIZE_MB)),
    tap((configuration) => this.attachmentMaxSizeMB = Number(configuration[0]?.value))
  ).subscribe();
  attachmentMaxSizeMB: number;

  constructor(private fb: FormBuilder, private store: Store<fromRoot.State>) { }

  ngOnInit(): void {
    this.attachmentForm = this.fb.group({
      id: [],
      type: ['', Validators.required],
      name:  ['', Validators.required],
      description: [],
      size: [0, Validators.max(this.attachmentMaxSizeMB * MB)],
      file: ['', [Validators.required, this.validateAttachment.bind(this)]],
      creationTime: [],
      decisionAttachment: [{value: false, disabled: this.decisionAttachmentDisabled}],
      handlerName: [],
    });

    this.attachmentForm.patchValue(AttachmentInfo.toForm(this.attachment));
    this.setValidForDecision(this.attachment.mimeType);

    if (!!this.attachment.id) {
      this.attachmentForm.disable();
    }
  }

  validateAttachment(control: FormControl) {
    const file = control.value;
    if (file) {
      const fileExtension = '.' + file.name.split('.').pop();
      const allowedFileTypes = this.attachmentAllowedTypes.split(',').map((type) => type.trim());
      if (!allowedFileTypes.find((allowedType) => allowedType === fileExtension)) {
        return { invalidExtension: true };
      }
    }
    return null;
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
      this.attachmentForm.patchValue({name: file.name, file: file, size: file.size});
      this.setValidForDecision(file.type);
    }
  }

  getMaxAttachmentSize(): string {
    return this.attachmentMaxSizeMB + ' MB';
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  private setValidForDecision(fileType: string): void {
    this.validForDecision = validForDecision(fileType);
    if (!this.validForDecision) {
      this.attachmentForm.get('decisionAttachment').patchValue(false);
    }
  }
}
