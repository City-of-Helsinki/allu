import {Component, forwardRef, HostBinding, Input} from '@angular/core';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';

export type Selected = undefined | 'old' | 'new';

export interface FieldSelection {
  [fieldName: string]: Selected;
}

const FIELD_ACCEPTANCE_VALUE_ACCESSOR = {
  provide: NG_VALUE_ACCESSOR,
  useExisting: forwardRef(() => FieldAcceptanceComponent),
  multi: true
};

@Component({
  selector: 'field-acceptance',
  templateUrl: './field-acceptance.component.html',
  styleUrls: ['./field-acceptance.component.scss'],
  providers: [FIELD_ACCEPTANCE_VALUE_ACCESSOR]
})
export class FieldAcceptanceComponent implements ControlValueAccessor {
  @Input() label: string;
  @Input() oldValue: any;
  @Input() newValue: any;
  @Input() readonly: boolean;

  @HostBinding('class') classNames = 'field-acceptance';

  selected: Selected;

  select(selected: Selected) {
    this.selected = selected;
    this._onChange(this.selected);
  }

  registerOnChange(fn: any): void {
    this._onChange = fn;
  }

  registerOnTouched(fn: any): void {}

  writeValue(selected: Selected): void {
    this.selected  = selected;
  }

  private _onChange = (_: any) => {};
}
