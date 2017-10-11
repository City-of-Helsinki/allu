import {AfterContentInit, Component, Input} from '@angular/core';
import {MatDialogRef} from '@angular/material';

import {ApplicationChange} from '../../../model/application/application-change/application-change';
import {User} from '../../../model/user/user';
import {StructureMeta} from '../../../model/application/meta/structure-meta';
import {ApplicationFieldChange} from '../../../model/application/application-change/application-field-change';
import {ApplicationHistoryFormatter} from '../../../service/history/application-history-formatter';

@Component({
  selector: 'application-history-details',
  template: require('./application-history-details.component.html'),
  styles: [
    require('./application-history-details.component.scss')
  ]
})
export class ApplicationHistoryDetailsComponent implements AfterContentInit {

  @Input() change: ApplicationChange;
  @Input() user: User;
  @Input() meta: StructureMeta;

  fieldChanges: Array<ApplicationFieldChange>;

  constructor(public dialogRef: MatDialogRef<ApplicationHistoryDetailsComponent>, private formatter: ApplicationHistoryFormatter) {}

  ngAfterContentInit(): void {
    this.formatter.setMeta(this.meta);
    this.fieldChanges = this.change.fieldChanges
      .map(fc => this.formatter.toFormattedChange(fc));
  }

  closeModal(): void {
    this.dialogRef.close();
  }
}
