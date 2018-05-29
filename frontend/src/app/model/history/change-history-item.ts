import {User} from '../user/user';
import {FieldChange} from './field-change';
import {TimeUtil} from '../../util/time.util';

export class ChangeHistoryItem {
  constructor(
    public user?: User,
    public changeType?: string,
    public newStatus?: string,
    public changeTime?: Date,
    public fieldChanges?: Array<FieldChange>
  ) {
    this.fieldChanges = fieldChanges || [];
  }

  get uiChangeTime(): string {
    return TimeUtil.getUiDateTimeString(this.changeTime);
  }
}
