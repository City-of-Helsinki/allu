import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Application} from '@model/application/application';
import {InfoAcceptance} from '@feature/information-request/acceptance/info-acceptance';
import {FieldLabels, FieldValues} from '@feature/information-request/acceptance/field-group-acceptance.component';
import {findTranslation} from '@util/translations';
import get from 'lodash/get';
import {FieldKeyMapping} from '@feature/information-request/acceptance/other/application-acceptance-field-mapping';

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
    const fieldInfo = FieldKeyMapping[fieldKey];
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
    const fieldInfo = FieldKeyMapping[fieldKey];
    const labels = {};
    if (fieldInfo) {
      labels[fieldInfo.fieldName] = findTranslation(['informationRequest.field', fieldKey]);
    }
    return labels;
  }
}
