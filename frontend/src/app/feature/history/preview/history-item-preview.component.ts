import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {ChangeHistoryItem} from '../../../model/history/change-history-item';
import {ChangeDescription, ChangeDescriptionType, HistoryFormatter} from '../../../service/history/history-formatter';
import {ChangeType} from '../../../model/history/change-type';

@Component({
  selector: 'history-item-preview',
  templateUrl: './history-item-preview.component.html',
  styleUrls: ['./history-item-preview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HistoryItemPreviewComponent implements OnInit {
  description: ChangeDescription;
  showFieldChangeCount: boolean;
  fieldChangeCount: number;

  private _change: ChangeHistoryItem;

  constructor(private formatter: HistoryFormatter) {}

  ngOnInit(): void {
  }

  @Input() set change(change: ChangeHistoryItem) {
    this._change = change;
    this.description = this.formatter.getChangeDescription(change, ChangeDescriptionType.SIMPLE);
    this.showFieldChangeCount = ChangeType.CONTENTS_CHANGED === ChangeType[change.changeType];
    this.fieldChangeCount = change.fieldChanges.length;
  }

  get change() {
    return this._change;
  }
}
