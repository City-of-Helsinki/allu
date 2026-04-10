import {Injectable} from '@angular/core';

import {StructureMeta} from '../../model/application/meta/structure-meta';
import {FieldChange, FieldChangeType} from '../../model/history/field-change';
import {AttributeDataType} from '../../model/application/meta/attribute-data-type';
import {TimeUtil} from '../../util/time.util';
import {findTranslation} from '../../util/translations';
import {Some} from '../../util/option';
import {StringUtil} from '../../util/string.util';
import {CityDistrict} from '../../model/common/city-district';
import {CodeSetCodeMap} from '../../model/codeset/codeset';
import * as fromRoot from '../../feature/allu/reducers';
import {Store} from '@ngrx/store';
import {take} from 'rxjs/internal/operators';

@Injectable()
export class HistoryFieldFormatter {

  private meta: StructureMeta;
  private cityDistricts: Array<CityDistrict> = [];
  private countries: CodeSetCodeMap = {};

  constructor(private store: Store<fromRoot.State>) {
    this.store.select(fromRoot.getAllCityDistricts).pipe(take(1))
      .subscribe(districts => this.cityDistricts = districts);
    this.store.select(fromRoot.getCodeSetCodeMap('Country')).pipe(take(1))
      .subscribe(countries => this.countries = countries);
  }

  public toFormattedChange(fieldChange: FieldChange, meta: StructureMeta): FieldChange {
    this.meta = meta;
    const dataType = this.meta.dataType(fieldChange.fieldName, '/');
    return this.formatByDataType(dataType, fieldChange);
  }

  private formatByDataType(dataType: string, fieldChange: FieldChange): FieldChange {
    switch (AttributeDataType[dataType]) {
      case AttributeDataType.DATETIME:
        return new FieldChange(
          fieldChange.fieldName,
          TimeUtil.formatHistoryDateTimeString(fieldChange.oldValue),
          TimeUtil.formatHistoryDateTimeString(fieldChange.newValue),
          this.meta.uiName(fieldChange.fieldName, '/'));
      case AttributeDataType.BOOLEAN:
        return new FieldChange(
          fieldChange.fieldName,
          this.formatAndTranslate('common.boolean', fieldChange.oldValue),
          this.formatAndTranslate('common.boolean', fieldChange.newValue),
          this.meta.uiName(fieldChange.fieldName, '/'));
      case AttributeDataType.ENUMERATION:
        return new FieldChange(
          fieldChange.fieldName,
          this.formatNonEmpty(fieldChange.fieldName, fieldChange.oldValue),
          this.formatNonEmpty(fieldChange.fieldName, fieldChange.newValue),
          this.meta.uiName(fieldChange.fieldName, '/'));
      default:
        return this.formatDefault(fieldChange);
    }
  }

  private formatAndTranslate(prefix: string, value: string) {
    return !StringUtil.isEmpty(value)
      ? findTranslation([prefix, value])
      : '';
  }

  private formatNonEmpty(path: string, value: string) {
    return Some(value)
      .filter(v => !StringUtil.isEmpty(v))
      .map(v => `${path}/${v}`)
      .map(valuePath => this.meta.uiName(valuePath, '/'))
      .orElse('');
  }

  private formatDefault(fieldChange: FieldChange): FieldChange {
    let oldValue = StringUtil.replaceNull(fieldChange.oldValue);
    let newValue = StringUtil.replaceNull(fieldChange.newValue);
    let uiFieldName = this.meta.uiName(fieldChange.fieldName, '/');

    switch (fieldChange.fieldChangeType) {
      case FieldChangeType.DISTRICT_ID:
        oldValue = Some(this.cityDistricts.find(d => String(d.id) === oldValue)).map(d => d.name).orElse(undefined);
        newValue = Some(this.cityDistricts.find(d => String(d.id) === newValue)).map(d => d.name).orElse(undefined);
        break;
      case FieldChangeType.COUNTRY_ID:
        oldValue = Some(Object.values(this.countries).find(c => String(c.id) === oldValue)).map(c => c.description).orElse(oldValue);
        newValue = Some(Object.values(this.countries).find(c => String(c.id) === newValue)).map(c => c.description).orElse(newValue);
        break;
      default:
        break;
    }
    return new FieldChange(fieldChange.fieldName, oldValue, newValue, uiFieldName);
  }
}
