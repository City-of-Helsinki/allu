import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {ChangeHistoryItem} from '../../model/history/change-history-item';
import {ChangeType} from '../../model/history/change-type';
import {Store} from '@ngrx/store';
import * as fromRoot from '../allu/reducers';
import {FieldChange} from '../../model/history/field-change';
import {ChangeDescription, HistoryFormatter} from '../../service/history/history-formatter';
import {StructureMeta} from '../../model/application/meta/structure-meta';
import {ArrayUtil} from '@util/array-util';

@Component({
  selector: 'history-item',
  templateUrl: './history-item.component.html',
  styleUrls: ['./history-item.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HistoryItemComponent implements OnInit {
  @Input() meta: StructureMeta;
  @Input() fieldsVisible: boolean;

  description: ChangeDescription;
  fieldChanges: FieldChange[] = [];

  private _change: ChangeHistoryItem;

  constructor(private store: Store<fromRoot.State>,
              private formatter: HistoryFormatter) {}

  ngOnInit(): void {
  }

  @Input() set change(change: ChangeHistoryItem) {
    this._change = change;
    this.description = this.formatter.getChangeDescription(change);
    this.fieldChanges = this.getFieldChanges(change);
  }

  get change() {
    return this._change;
  }

  private getFieldChanges(change: ChangeHistoryItem) {
    return this.showFields(change)
      ? change.fieldChanges
      : [];
  }

  private showFields(change: ChangeHistoryItem): boolean {
    return ArrayUtil.contains([ChangeType.CONTENTS_CHANGED, ChangeType.LOCATION_CHANGED], ChangeType[change.changeType]);
  }
}
