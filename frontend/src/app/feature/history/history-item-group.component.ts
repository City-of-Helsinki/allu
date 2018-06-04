import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ChangeHistoryItem} from '../../model/history/change-history-item';

@Component({
  selector: 'history-item-group',
  templateUrl: './history-item-group.component.html',
  styleUrls: ['./history-item-group.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HistoryItemGroupComponent {
  @Input() title: string;
  @Input() changes: ChangeHistoryItem[];
}
