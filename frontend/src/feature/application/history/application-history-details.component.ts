import {AfterContentInit, Component, Input, OnInit} from '@angular/core';
import {MdDialogRef} from '@angular/material';

import {ApplicationChange} from '../../../model/application/application-change/application-change';
import {User} from '../../../model/common/user';
import {StructureMeta} from '../../../model/application/meta/structure-meta';
import {ApplicationFieldChange} from '../../../model/application/application-change/application-field-change';
import {AttributeDataType} from '../../../model/application/meta/attribute-data-type';
import {TimeUtil} from '../../../util/time.util';
import {findTranslation} from '../../../util/translations';
import {StringUtil} from '../../../util/string.util';
import {Some} from '../../../util/option';
import {CityDistrict} from '../../../model/common/city-district';
import {MapHub} from '../../../service/map/map-hub';

const DISTRICT_ID_CHANGE = 'cityDistrictId';

@Component({
  selector: 'application-history-details',
  template: require('./application-history-details.component.html'),
  styles: [
    require('./application-history-details.component.scss')
  ]
})
export class ApplicationHistoryDetailsComponent implements OnInit, AfterContentInit {

  @Input() change: ApplicationChange;
  @Input() user: User;
  @Input() meta: StructureMeta;

  fieldChanges: Array<ApplicationFieldChange>;

  private cityDistricts: Array<CityDistrict> = [];

  constructor(public dialogRef: MdDialogRef<ApplicationHistoryDetailsComponent>, private mapHub: MapHub) {}

  ngOnInit(): void {
    this.mapHub.districts()
      .subscribe(districts => this.cityDistricts = districts)
      .unsubscribe();
  }

  ngAfterContentInit(): void {
    this.fieldChanges = this.change.fieldChanges
      .map(fc => this.toFormattedChange(fc));
  }

  private toFormattedChange(fieldChange: ApplicationFieldChange): ApplicationFieldChange {
    let dataType = this.meta.dataType(fieldChange.fieldName);
    return this.formatByDataType(dataType, fieldChange);
  }

  private formatByDataType(dataType: string, fieldChange: ApplicationFieldChange): ApplicationFieldChange {
    switch (AttributeDataType[dataType]) {
      case AttributeDataType.DATETIME:
        return new ApplicationFieldChange(
          this.meta.uiName(fieldChange.fieldName),
            TimeUtil.formatHistoryDateTimeString(fieldChange.oldValue),
            TimeUtil.formatHistoryDateTimeString(fieldChange.newValue));
      case AttributeDataType.BOOLEAN:
        return new ApplicationFieldChange(
          this.meta.uiName(fieldChange.fieldName),
            findTranslation(['common.boolean', fieldChange.oldValue]),
            findTranslation(['common.boolean', fieldChange.newValue]));
      case AttributeDataType.ENUMERATION:
        return new ApplicationFieldChange(
          this.meta.uiName(fieldChange.fieldName),
          this.formatNonEmpty(fieldChange.fieldName, fieldChange.oldValue),
          this.formatNonEmpty(fieldChange.fieldName, fieldChange.newValue));
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

    if (fieldChange.fieldName.indexOf(DISTRICT_ID_CHANGE) >= 0) {
      oldValue = Some(this.cityDistricts.find(d => String(d.id) === oldValue)).map(d => d.name).orElse(undefined);
      newValue = Some(this.cityDistricts.find(d => String(d.id) === newValue)).map(d => d.name).orElse(undefined);
    }

    return new ApplicationFieldChange(
      this.meta.uiName(fieldChange.fieldName),
      StringUtil.replaceNull(oldValue),
      StringUtil.replaceNull(newValue));
  }
}
