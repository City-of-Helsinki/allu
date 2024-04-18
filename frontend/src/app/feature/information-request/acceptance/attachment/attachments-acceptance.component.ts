import {ChangeDetectionStrategy, Component, HostBinding, Input, OnInit} from '@angular/core';
import {AttachmentInfo} from '@model/application/attachment/attachment-info';
import {TimeUtil} from '@util/time.util';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'attachments-acceptance',
  templateUrl: './attachments-acceptance.component.html',
  styleUrls: [
    '../info-acceptance/info-acceptance.component.scss',
    '../field-select/field-select.component.scss'
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AttachmentsAcceptanceComponent implements OnInit {

  @HostBinding('class') cssClasses = 'info-acceptance';

  @Input() parentForm: UntypedFormGroup;

  private _attachments: AttachmentInfo[] = [];
  private form: UntypedFormGroup;

  constructor(private fb: UntypedFormBuilder) {
    this.form = fb.group({
      attachments: [undefined, Validators.requiredTrue]
    });
  }

  ngOnInit(): void {
    this.parentForm.addControl('attachments', this.form);
  }

  @Input() set attachments(attachments: AttachmentInfo[]) {
    this._attachments = [...attachments];
    this._attachments.sort((left, right) => TimeUtil.compareTo(left.creationTime, right.creationTime));
    this.form.patchValue({attachments: true});
  }

  get attachments() {
    return this._attachments;
  }
}
