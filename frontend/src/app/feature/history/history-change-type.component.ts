import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ChangeHistoryItem} from '@model/history/change-history-item';

@Component({
  selector: 'history-change-type',
  templateUrl: './history-change-type.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HistoryChangeTypeComponent {
  @Input() change: ChangeHistoryItem;
}
