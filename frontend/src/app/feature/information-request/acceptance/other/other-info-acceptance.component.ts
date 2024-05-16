import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Application} from '@model/application/application';
import {InfoAcceptanceDirective} from '@feature/information-request/acceptance/info-acceptance/info-acceptance.component';
import get from 'lodash/get';
import isEqual from 'lodash/isEqual';
import {FieldKeyMapping} from '@feature/information-request/acceptance/other/application-acceptance-field-mapping';
import {UntypedFormBuilder} from '@angular/forms';
import {FieldValues} from '@feature/information-request/acceptance/field-select/field-select.component';
import {FieldDescription} from '@feature/information-request/acceptance/field-select/field-description';
import {ApplicationExtension} from '@model/application/type/application-extension';
import {ArrayUtil} from '@util/array-util';
import {formatValue, StructureMeta} from '@model/application/meta/structure-meta';
import {blacklistForType} from '@feature/information-request/acceptance/other/field-update-rules';

const requiredFields = {
  startTime: true,
  endTime: true
};

@Component({
  selector: 'other-info-acceptance',
  templateUrl: '../info-acceptance/info-acceptance.component.html',
  styleUrls: ['../info-acceptance/info-acceptance.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OtherInfoAcceptanceComponent extends InfoAcceptanceDirective<any> implements OnInit {
  @Input() oldInfo: Application;
  @Input() newInfo: Application;
  @Input() fieldKeys: string[];
  @Input() meta: StructureMeta;

  @Output() otherInfoChanges = new EventEmitter<FieldValues>();

  constructor(fb: UntypedFormBuilder) {
    super(fb);
  }

  ngOnInit(): void {
    const keys = this.getFieldKeys();

    this.oldValues = this.toFieldValues(this.oldInfo, keys);
    this.oldDisplayValues = this.toDisplayValues(this.oldValues);

    this.newValues = this.toFieldValues(this.newInfo, keys);
    this.newDisplayValues = this.toDisplayValues(this.newValues);
    this.fieldDescriptions = this.createDescriptions(keys);

    super.ngOnInit();
  }

  protected resultChanges(result: FieldValues): void {
    this.otherInfoChanges.emit(result);
  }

  protected isRequired(field: string): boolean {
    return requiredFields[field];
  }

  private getFieldKeys(notifiedFieldKeys: string[] = []): string[] {
    const fieldKeys = notifiedFieldKeys.map(key => FieldKeyMapping[key]);
    return this.getChangedFields(this.oldInfo.extension, this.newInfo.extension)
      .concat(fieldKeys)
      .filter(ArrayUtil.unique)
      .filter(key => blacklistForType(this.oldInfo.type).indexOf(key) < 0);
  }

  private toFieldValues(application: Application, fieldKeys: string[]): FieldValues {
    return fieldKeys.reduce((values: FieldValues, key: string) => {
      const newValue = this.toFieldValue(key, application);
      return {
        ...values,
        ...newValue
      };
    }, {});
  }

  private toDisplayValues(fieldValues: FieldValues): FieldValues {
    return {...fieldValues};
  }

  private toFieldValue(fieldKey: string, application: Application): FieldValues {
    const fieldValues = {};
    fieldValues[fieldKey] = get(application, fieldKey);
    return fieldValues;
  }

  private createDescriptions(fieldKeys: string[]): FieldDescription[] {
    return fieldKeys
      .map(key => this.createDescription(key))
      .filter(desc => !!desc);
  }

  private createDescription(fieldKey: string): FieldDescription {
    return new FieldDescription(fieldKey, this.meta.uiName(fieldKey));
  }

  private getChangedFields(oldExtension: ApplicationExtension, newExtension: ApplicationExtension): string[] {
    return Object.keys(newExtension).reduce((changedFields, field) => {
      if (this.fieldHasChange(get(oldExtension, field), get(newExtension, field))) {
        return changedFields.concat(`extension.${field}`);
      } else {
        return changedFields;
      }
    }, []);
  }

  fieldHasChange<T>(oldValue: T, newValue: T): boolean {
    // Check with == to handle null and undefined
    if (oldValue == null && newValue == null) {
      return false;
    } else {
      return !isEqual(oldValue, newValue);
    }
  }
}
