import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Application} from '@model/application/application';
import {InfoAcceptanceComponent} from '@feature/information-request/acceptance/info-acceptance/info-acceptance.component';
import {findTranslation} from '@util/translations';
import get from 'lodash/get';
import {FieldKeyMapping} from '@feature/information-request/acceptance/other/application-acceptance-field-mapping';
import {FormBuilder} from '@angular/forms';
import {FieldValues} from '@feature/information-request/acceptance/field-select/field-select.component';
import {FieldDescription} from '@feature/information-request/acceptance/field-select/field-description';

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
export class OtherInfoAcceptanceComponent extends InfoAcceptanceComponent<any> implements OnInit {
  @Input() oldInfo: Application;
  @Input() newInfo: Application;
  @Input() readonly: boolean;
  @Input() fieldKeys: string[];

  @Output() otherInfoChanges = new EventEmitter<FieldValues>();

  constructor(fb: FormBuilder) {
    super(fb);
  }

  ngOnInit(): void {
    this.oldValues = this.toFieldValues(this.oldInfo);
    this.oldDisplayValues = this.toDisplayValues(this.oldValues);

    this.newValues = this.toFieldValues(this.newInfo);
    this.newDisplayValues = this.toDisplayValues(this.newValues);
    this.fieldDescriptions = this.createDescriptions();

    super.ngOnInit();
  }

  protected resultChanges(result: FieldValues): void {
    this.otherInfoChanges.emit(result);
  }

  protected isRequired(field: string): boolean {
    return requiredFields[field];
  }

  private toFieldValues(application: Application): FieldValues {
    return this.fieldKeys.reduce((values: FieldValues, key: string) => {
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
    const fieldInfo = FieldKeyMapping[fieldKey];
    const fieldValues = {};
    if (fieldInfo) {
      fieldValues[fieldInfo.fieldName] = get(application, fieldInfo.valueField);
    }
    return fieldValues;
  }

  private createDescriptions(): FieldDescription[] {
    return this.fieldKeys
      .map(key => this.createDescription(key))
      .filter(desc => !!desc);
  }

  private createDescription(fieldKey: string): FieldDescription {
    const fieldInfo = FieldKeyMapping[fieldKey];
    if (fieldInfo) {
      return new FieldDescription(fieldInfo.fieldName, findTranslation(['informationRequest.field', fieldKey]));
    } else {
      return undefined;
    }
  }
}
