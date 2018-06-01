import {ApplicationFieldChange} from './application-field-change';
import {TimeUtil} from '../../../util/time.util';
import {User} from '../../user/user';

export class ApplicationChange {
  constructor(
    public user?: User,
    public changeType?: string,
    public newStatus?: string,
    public changeTime?: Date,
    public fieldChanges?: Array<ApplicationFieldChange>
  ) {
    this.fieldChanges = fieldChanges || [];
  }

  get uiChangeTime(): string {
    return TimeUtil.getUiDateTimeString(this.changeTime);
  }
}
