import {Component, Input, AfterContentInit} from '@angular/core';
import {Observable} from 'rxjs/Observable';

import {ApplicationChange} from '../../../model/application/application-change/application-change';
import {User} from '../../../model/common/user';
import {MdDialogRef} from '@angular/material';
import {StructureMeta} from '../../../model/application/meta/structure-meta';
import {ApplicationFieldChange} from '../../../model/application/application-change/application-field-change';
import {AttributeDataType} from '../../../model/application/meta/attribute-data-type';
import {TimeUtil} from '../../../util/time.util';
import {findTranslation} from '../../../util/translations';
import {StringUtil} from '../../../util/string.util';

@Component({
  selector: 'application-history-details',
  template: require('./application-history-details.component.html'),
  styles: []
})
export class ApplicationHistoryDetailsComponent implements AfterContentInit {

  @Input() change: ApplicationChange;
  @Input() user: User;
  @Input() meta: StructureMeta;

  fieldChanges: Array<ApplicationFieldChange>;

  constructor(public dialogRef: MdDialogRef<ApplicationHistoryDetailsComponent>) {}


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
      default:
        return new ApplicationFieldChange(
          this.meta.uiName(fieldChange.fieldName),
            StringUtil.replaceNull(fieldChange.oldValue),
            StringUtil.replaceNull(fieldChange.newValue));
    }
  }
}
