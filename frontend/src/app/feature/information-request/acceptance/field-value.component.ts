import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {typeOfValue, ValueType} from '../../../util/object.util';
import {findTranslation} from '../../../util/translations';

@Component({
  selector: 'field-value',
  templateUrl: './field-value.component.html',
  styleUrls: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FieldValueComponent implements OnInit {
  @Input() translationPrefix: string;
  @Input() translationSuffix: string;

  valueType: ValueType;
  displayValue: any;

  ngOnInit(): void {
    this.valueType = typeOfValue(this.value);
  }

  @Input() set value(value: any) {
    const translationKey = [this.translationPrefix, value, this.translationSuffix].filter(val => val !== undefined);
    const translate = value !== undefined && translationKey.length > 1;
    this.displayValue = translate ? findTranslation(translationKey) : value;
  }
}
