import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ChangeHistoryItem} from '@model/history/change-history-item';

@Component({
  selector: 'history-item-status',
  templateUrl: './history-item-status.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HistoryItemStatusComponent {
  @Input() change: ChangeHistoryItem;
}
