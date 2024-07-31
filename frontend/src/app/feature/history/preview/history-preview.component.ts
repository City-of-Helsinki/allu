import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import {ChangeHistoryItem} from '../../../model/history/change-history-item';

@Component({
  selector: 'history-preview',
  templateUrl: './history-preview.component.html',
  styleUrls: ['./history-preview.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HistoryPreviewComponent {
  @Input() historyLink: (string | number)[];

  visibleChanges: ChangeHistoryItem[] = [];

  @Input('changes') set changes(changes: ChangeHistoryItem[]) {
    this.visibleChanges = changes
      ? changes.slice(0, 5)
      : [];
  }
}
