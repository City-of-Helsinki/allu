import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnChanges} from '@angular/core';
import {FieldChange} from '../../../model/history/field-change';
import {StructureMeta} from '../../../model/application/meta/structure-meta';
import {HistoryFieldFormatter} from '../../../service/history/history-field-formatter';

@Component({
  selector: 'history-field',
  templateUrl: './history-field.component.html',
  styleUrls: ['./history-field.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HistoryFieldComponent implements OnChanges {
  @Input() meta: StructureMeta;
  @Input() fieldChange: FieldChange;

  content: FieldChange;

  constructor(private formatter: HistoryFieldFormatter, private cdr: ChangeDetectorRef) {}

  ngOnChanges(): void {
    if (this.fieldChange && this.meta) {
      this.content = this.formatter.toFormattedChange(this.fieldChange, this.meta);
      this.cdr.markForCheck();
    }
  }
}
