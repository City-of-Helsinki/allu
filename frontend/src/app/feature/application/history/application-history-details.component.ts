import {AfterContentInit, Component, Input} from '@angular/core';
import {MatDialogRef} from '@angular/material';

import {ChangeHistoryItem} from '../../../model/history/change-history-item';
import {User} from '../../../model/user/user';
import {StructureMeta} from '../../../model/application/meta/structure-meta';
import {FieldChange} from '../../../model/history/field-change';
import {HistoryFieldFormatter} from '../../../service/history/history-field-formatter';

@Component({
  selector: 'application-history-details',
  templateUrl: './application-history-details.component.html',
  styleUrls: [
    './application-history-details.component.scss'
  ]
})
export class ApplicationHistoryDetailsComponent implements AfterContentInit {

  @Input() change: ChangeHistoryItem;
  @Input() user: User;
  @Input() meta: StructureMeta;

  fieldChanges: Array<FieldChange>;

  constructor(public dialogRef: MatDialogRef<ApplicationHistoryDetailsComponent>, private formatter: HistoryFieldFormatter) {}

  ngAfterContentInit(): void {
    this.fieldChanges = this.change.fieldChanges
      .map(fc => this.formatter.toFormattedChange(fc, this.meta));
  }

  closeModal(): void {
    this.dialogRef.close();
  }
}
