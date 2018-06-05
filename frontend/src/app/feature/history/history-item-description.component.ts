import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ChangeItemDescription} from '../../service/history/history-formatter';

@Component({
  selector: 'history-item-description',
  templateUrl: './history-item-description.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HistoryItemDescriptionComponent {
  @Input() changeItem: ChangeItemDescription;
}
