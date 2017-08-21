import {AfterContentInit, Component, Input, OnInit} from '@angular/core';
import {MdDialogRef} from '@angular/material';

import {ApplicationChange} from '../../../model/application/application-change/application-change';
import {User} from '../../../model/common/user';
import {StructureMeta} from '../../../model/application/meta/structure-meta';
import {ApplicationFieldChange, FieldChangeType} from '../../../model/application/application-change/application-field-change';
import {AttributeDataType} from '../../../model/application/meta/attribute-data-type';
import {TimeUtil} from '../../../util/time.util';
import {findTranslation} from '../../../util/translations';
import {StringUtil} from '../../../util/string.util';
import {Some} from '../../../util/option';
import {CityDistrict} from '../../../model/common/city-district';
import {MapHub} from '../../../service/map/map-hub';
import {ApplicationHistoryFormatter} from '../../../service/history/application-history-formatter';

@Component({
  selector: 'application-history-details',
  template: require('./application-history-details.component.html'),
  styles: [
    require('./application-history-details.component.scss')
  ]
})
export class ApplicationHistoryDetailsComponent implements AfterContentInit {

  @Input() change: ApplicationChange;
  @Input() user: User;
  @Input() meta: StructureMeta;

  fieldChanges: Array<ApplicationFieldChange>;

  constructor(public dialogRef: MdDialogRef<ApplicationHistoryDetailsComponent>, private formatter: ApplicationHistoryFormatter) {}

  ngAfterContentInit(): void {
    this.formatter.setMeta(this.meta);
    this.fieldChanges = this.change.fieldChanges
      .map(fc => this.formatter.toFormattedChange(fc));
  }

  closeModal(): void {
    this.dialogRef.close();
  }
}
