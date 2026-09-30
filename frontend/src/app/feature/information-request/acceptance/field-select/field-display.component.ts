import {ChangeDetectionStrategy, Component, Input} from '@angular/core';
import isEqual from 'lodash/isEqual';
import {FieldDescription} from '@feature/information-request/acceptance/field-select/field-description';
import {FieldValues} from '@feature/information-request/acceptance/field-select/field-select.component';
import {MapFeature} from '@feature/map/map-feature';
import {pathStyle} from '@service/map/map-draw-styles';
import {StructureMeta} from '@model/application/meta/structure-meta';

@Component({
  selector: 'field-display',
  templateUrl: './field-display.component.html',
  styleUrls: ['./field-select.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class FieldDisplayComponent {
  @Input() id = '';
  @Input() descriptions: FieldDescription[] = [];
  @Input() meta: StructureMeta;
  @Input() selected = false;

  private _fieldValues: FieldValues;
  private _comparedValues: FieldValues;

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

  fieldHasChange(field: string): boolean {
    if (this.fieldValues && this.comparedValues) {
      const value = this.fieldValues[field];
      const comparedValue = this.comparedValues[field];

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
