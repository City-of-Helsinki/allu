import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {ChangeHistoryItem} from '../../model/history/change-history-item';
import {ChangeType} from '../../model/history/change-type';
import {Store} from '@ngrx/store';
import * as fromRoot from '../allu/reducers';
import {FieldChange} from '../../model/history/field-change';
import {ChangeDescription, HistoryFormatter} from '../../service/history/history-formatter';

@Component({
  selector: 'history-item',
  templateUrl: './history-item.component.html',
  styleUrls: ['./history-item.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HistoryItemComponent implements OnInit {
  time: Date;
  type: string;
  description: ChangeDescription;
  fieldChanges: FieldChange[] = [];
  user: string;

  constructor(private store: Store<fromRoot.State>,
              private formatter: HistoryFormatter) {}

  ngOnInit(): void {
  }

  @Input() set change(change: ChangeHistoryItem) {
    this.time = change.changeTime;
    this.type = change.changeType;
    this.user = change.user ? change.user.realName : undefined;
    this.description = this.formatter.getChangeDescription(change);
    this.fieldChanges = this.getFieldChanges(change);
  }

  private getFieldChanges(change: ChangeHistoryItem) {
    return ChangeType.CONTENTS_CHANGED === ChangeType[change.changeType]
      ? change.fieldChanges
      : [];
  }
}
