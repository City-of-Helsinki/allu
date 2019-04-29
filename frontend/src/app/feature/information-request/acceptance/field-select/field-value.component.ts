import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {typeOfValue, ValueType} from '@util/object.util';
import {TimeUtil} from '@util/time.util';
import {formatValue, StructureMeta} from '@model/application/meta/structure-meta';

@Component({
  selector: 'field-value',
  templateUrl: './field-value.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FieldValueComponent implements OnInit {
  @Input() field: string;
  @Input() meta: StructureMeta;

  valueType: ValueType;
  displayValue: any;

  ngOnInit(): void {
    this.valueType = typeOfValue(this.value);
  }

  @Input() set value(value: any) {
    if (value instanceof Date) {
      this.displayValue = TimeUtil.getUiDateString(value);
    } else if (this.meta && this.meta.contains(this.field)) {
      this.displayValue = formatValue(this.field, value, this.meta);
    } else {
      this.displayValue = value;
    }
  }
}
