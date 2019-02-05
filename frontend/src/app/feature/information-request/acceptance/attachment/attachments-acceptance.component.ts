import {ChangeDetectionStrategy, Component, HostBinding, Input, OnInit} from '@angular/core';
import {AttachmentInfo} from '@model/application/attachment/attachment-info';
import {ArrayUtil} from '@util/array-util';
import {TimeUtil} from '@util/time.util';

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

  private _attachments: AttachmentInfo[] = [];

  constructor() {}

  ngOnInit(): void {
  }

  @Input() set attachments(attachments: AttachmentInfo[]) {
    this._attachments = [...attachments];
    this._attachments.sort((left, right) => TimeUtil.compareTo(left.creationTime, right.creationTime));
  }

  get attachments() {
    return this._attachments;
  }
}
