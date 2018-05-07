import {Injectable} from '@angular/core';

import {StructureMeta} from '../../model/application/meta/structure-meta';
import {ApplicationFieldChange, FieldChangeType} from '../../model/application/application-change/application-field-change';
import {AttributeDataType} from '../../model/application/meta/attribute-data-type';
import {TimeUtil} from '../../util/time.util';
import {findTranslation} from '../../util/translations';
import {Some} from '../../util/option';
import {StringUtil} from '../../util/string.util';
import {CityDistrict} from '../../model/common/city-district';
import * as fromRoot from '../../feature/allu/reducers';
import {Store} from '@ngrx/store';

@Injectable()
export class ApplicationHistoryFormatter {

  private meta: StructureMeta;
  private cityDistricts: Array<CityDistrict> = [];

  constructor(private store: Store<fromRoot.State>) {
    this.store.select(fromRoot.getAllCityDistricts).take(1)
      .subscribe(districts => this.cityDistricts = districts);
  }

  public setMeta(meta: StructureMeta) {
    this.meta = meta;
  }

  public toFormattedFieldNames(fieldChange: ApplicationFieldChange): ApplicationFieldChange {
    let uiFieldName = this.meta.uiName(fieldChange.fieldName);

    switch (fieldChange.fieldChangeType) {
      case FieldChangeType.CUSTOMER:
      case FieldChangeType.CONTACT:
        uiFieldName = findTranslation(['history.change.field', fieldChange.uiFieldChangeType]) + ' '
          + findTranslation(['history.change.operation', fieldChange.uiFieldChangeOperationType]);
        break;
      default:
    }
    return new ApplicationFieldChange(fieldChange.fieldName, fieldChange.oldValue, fieldChange.newValue, uiFieldName);
  }

  public toFormattedChange(fieldChange: ApplicationFieldChange): ApplicationFieldChange {
    const dataType = this.meta.dataType(fieldChange.fieldName);
    return this.formatByDataType(dataType, fieldChange);
  }

  private formatByDataType(dataType: string, fieldChange: ApplicationFieldChange): ApplicationFieldChange {
    switch (AttributeDataType[dataType]) {
      case AttributeDataType.DATETIME:
        return new ApplicationFieldChange(
          fieldChange.fieldName,
          TimeUtil.formatHistoryDateTimeString(fieldChange.oldValue),
          TimeUtil.formatHistoryDateTimeString(fieldChange.newValue),
          this.meta.uiName(fieldChange.fieldName));
      case AttributeDataType.BOOLEAN:
        return new ApplicationFieldChange(
          fieldChange.fieldName,
          Some(fieldChange.oldValue).map(val => findTranslation(['common.boolean', val])).orElse(''),
          Some(fieldChange.newValue).map(val => findTranslation(['common.boolean', val])).orElse(''),
          this.meta.uiName(fieldChange.fieldName));
      case AttributeDataType.ENUMERATION:
        return new ApplicationFieldChange(
          fieldChange.fieldName,
          this.formatNonEmpty(fieldChange.fieldName, fieldChange.oldValue),
          this.formatNonEmpty(fieldChange.fieldName, fieldChange.newValue),
          this.meta.uiName(fieldChange.fieldName));
      default:
        return this.formatDefault(fieldChange);
    }
  }

  private formatNonEmpty(path: string, value: string) {
    return Some(value)
      .filter(v => !StringUtil.isEmpty(v))
      .map(v => this.meta.uiName(path, v))
      .orElse('');
  }

  private formatDefault(fieldChange: ApplicationFieldChange): ApplicationFieldChange {
    let oldValue = StringUtil.replaceNull(fieldChange.oldValue);
    let newValue = StringUtil.replaceNull(fieldChange.newValue);
    let uiFieldName = this.meta.uiName(fieldChange.fieldName);

    switch (fieldChange.fieldChangeType) {
      case FieldChangeType.DISTRICT_ID:
        oldValue = Some(this.cityDistricts.find(d => String(d.id) === oldValue)).map(d => d.name).orElse(undefined);
        newValue = Some(this.cityDistricts.find(d => String(d.id) === newValue)).map(d => d.name).orElse(undefined);
        break;
      case FieldChangeType.CUSTOMER:
      case FieldChangeType.CONTACT:
        uiFieldName = findTranslation(['history.change.field', fieldChange.uiFieldChangeType]) + ' '
          + findTranslation(['history.change.operation', fieldChange.uiFieldChangeOperationType]);
        break;
      default:
        break;
    }
    return new ApplicationFieldChange(fieldChange.fieldName, oldValue, newValue, uiFieldName);
  }
}
