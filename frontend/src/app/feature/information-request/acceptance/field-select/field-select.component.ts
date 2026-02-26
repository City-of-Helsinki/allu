import {ChangeDetectionStrategy, Component, forwardRef, Input, OnInit, ViewChild, ChangeDetectorRef} from '@angular/core';
import isEqual from 'lodash/isEqual';
import {MatLegacySelectionList as MatSelectionList} from '@angular/material/legacy-list';
import {ControlValueAccessor, NG_VALUE_ACCESSOR} from '@angular/forms';
import {FieldDescription} from '@feature/information-request/acceptance/field-select/field-description';
import {MapFeature} from '@feature/map/map-feature';
import {pathStyle} from '@service/map/map-draw-styles';
import {StructureMeta} from '@model/application/meta/structure-meta';

export interface FieldValues {
  [field: string]: any;
}

const FIELD_SELECT_VALUE_ACCESSOR = {
  provide: NG_VALUE_ACCESSOR,
  useExisting: forwardRef(() => FieldSelectComponent),
  multi: true
};

@Component({
  selector: 'field-select',
  templateUrl: './field-select.component.html',
  styleUrls: ['./field-select.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [FIELD_SELECT_VALUE_ACCESSOR]
})
export class FieldSelectComponent implements OnInit, ControlValueAccessor {

  constructor(private cdr: ChangeDetectorRef) {}

  @Input() id = '';
  @Input() descriptions: FieldDescription[] = [];
  @Input() meta: StructureMeta;

  @ViewChild(MatSelectionList, { static: true }) selectionList: MatSelectionList;

  isDisabled: boolean;
  displayMap = false;

  private _fieldValues: FieldValues;
  private _comparedValues: FieldValues;

  ngOnInit(): void {
    // fixes the map not always being displayed
    setTimeout(() => {
      this.displayMap = true;
      this.cdr.detectChanges();
    }, 0);
  }

  /** Implemented as part of ControlValueAccessor. */
  writeValue(values: string[]): void {
    if (this.selectionList) {
      this.selectionList.writeValue(values);
    }
  }

  /** Implemented as a part of ControlValueAccessor. */
  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
    if (this.selectionList) {
      this.selectionList.setDisabledState(isDisabled);
    }
  }

  /** Implemented as part of ControlValueAccessor. */
  registerOnChange(fn: (value: any) => void): void {
    if (this.selectionList) {
      this.selectionList.registerOnChange(fn);
    }
  }

  /** Implemented as part of ControlValueAccessor. */
  registerOnTouched(fn: () => void): void {
    if (this.selectionList) {
      this.selectionList.registerOnTouched(fn);
    }
  }

  @Input()
  get fieldValues() { return this._fieldValues; }
  set fieldValues(fieldValues: FieldValues) {
    this._fieldValues = fieldValues;
  }

  @Input()
  get comparedValues() { return this._comparedValues; }
  set comparedValues(comparedValues: FieldValues) {
    this._comparedValues = comparedValues;
  }

  get showSelectAll() {
    return !this.isDisabled && (this.descriptions.length > 1);
  }

  isSelected(field: string): boolean {
    return this.selectedValues.indexOf(field) > -1;
  }

  selectAll(): void {
    if (this.selectionList && this.selectionList.options) {
      this.selectionList.selectAll();
    }
  }

  deselectAll(): void {
    if (this.selectionList && this.selectionList.options) {
      this.selectionList.deselectAll();
    }
  }

  deselect(field: string): void {
    if (this.selectionList) {
      const updatedValues = this.selectedValues.filter(f => f !== field);
      this.selectionList.writeValue(updatedValues);
    }
  }

  get selectedValues(): string[] {
    if (this.selectionList && this.selectionList.options) {
      return this.selectionList.selectedOptions.selected.map(opt => opt.value);
    } else {
      return [];
    }
  }

  fieldHasChange(field: string): boolean {
    if (this.fieldValues && this.comparedValues) {
      const value = this.fieldValues[field];
      const comparedValue = this.comparedValues[field];

      // Check with == to handle null and undefined
      if (value == null && comparedValue == null) {
        return false;
      } else {
        return !isEqual(value, comparedValue);
      }
    }
  }

  getMapContent(field: string): MapFeature[] {
    if (this.fieldHasChange(field)) {
      return [{id: this.fieldValues['id'], geometry: this.fieldValues[field], style: pathStyle.WARNING}];
    } else {
      return [{id: this.fieldValues['id'], geometry: this.fieldValues[field]}];
    }
  }
}
