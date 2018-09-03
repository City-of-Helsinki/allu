import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Application} from '@model/application/application';
import {InfoAcceptance} from '@feature/information-request/acceptance/info-acceptance';
import {FieldLabels, FieldValues} from '@feature/information-request/acceptance/field-group-acceptance.component';
import {findTranslation} from '@util/translations';
import get from 'lodash/get';

const applicationFields = {
  START_TIME: {
    fieldName: 'startTime',
    valueField: 'startTime'
  },
  END_TIME: {
    fieldName: 'endTime',
    valueField: 'endTime'
  },
  IDENTIFICATION_NUMBER: {
    fieldName: 'identificationNumber',
    valueField: 'identificationNumber'
  },
  WORK_DESCRIPTION: {
    fieldName: 'workDescription',
    valueField: 'extension.additionalInfo'
  },
  PROPERTY_IDENTIFICATION_NUMBER: {
    fieldName: 'propertyIdentificationNumber',
    valueField: 'extension.propertyIdentificationNumber'
  }
};

@Component({
  selector: 'other-info-acceptance',
  templateUrl: './other-info-acceptance.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class OtherInfoAcceptanceComponent extends InfoAcceptance<any> implements OnInit {
  @Input() oldInfo: Application;
  @Input() newInfo: Application;
  @Input() readonly: boolean;
  @Input() fieldKeys: string[];

  @Output() otherInfoChanges = new EventEmitter<FieldValues>();

  ngOnInit(): void {
    this.oldValues = this.toFieldValues(this.oldInfo);
    this.oldDisplayValues = this.toDisplayValues(this.oldValues);

    this.newValues = this.toFieldValues(this.newInfo);
    this.newDisplayValues = this.toDisplayValues(this.newValues);
    this.fieldLabels = this.createLabels();

    super.ngOnInit();
  }

  protected resultChanges(result: FieldValues): void {
    this.otherInfoChanges.emit(result);
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
    const fieldInfo = applicationFields[fieldKey];
    const fieldValues = {};
    if (fieldInfo) {
      fieldValues[fieldInfo.fieldName] = get(application, fieldInfo.valueField);
    }
    return fieldValues;
  }

  private createLabels(): FieldLabels {
    return this.fieldKeys.reduce((labels: FieldLabels, key: string) => {
      const newLabel = this.createLabel(key);
      return {
        ...labels,
        ...newLabel
      };
    }, {});
  }

  private createLabel(fieldKey: string): FieldLabels {
    const fieldInfo = applicationFields[fieldKey];
    const labels = {};
    if (fieldInfo) {
      labels[fieldInfo.fieldName] = findTranslation(['informationRequest.field', fieldKey]);
    }
    return labels;
  }
}
