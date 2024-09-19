import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {FieldChange} from '../../../model/history/field-change';
import {StructureMeta} from '../../../model/application/meta/structure-meta';
import {HistoryFieldFormatter} from '../../../service/history/history-field-formatter';

@Component({
  selector: 'history-field',
  templateUrl: './history-field.component.html',
  styleUrls: ['./history-field.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HistoryFieldComponent implements OnInit {
  @Input() meta: StructureMeta;

  content: FieldChange;

  constructor(private formatter: HistoryFieldFormatter) {}

  ngOnInit(): void {
  }

  @Input() set fieldChange(fieldChange: FieldChange) {
  setTimeout(() => {
    if (this.meta) {
      this.content = this.formatter.toFormattedChange(fieldChange, this.meta);
    }
  }, 0)
  }
}
