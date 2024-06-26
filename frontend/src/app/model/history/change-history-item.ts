import {User} from '../user/user';
import {FieldChange} from './field-change';
import {TimeUtil} from '../../util/time.util';
import {ChangeHistoryItemInfo} from './change-history-item-info';

export class ChangeHistoryItem {
  constructor(
    public user?: User,
    public info?: ChangeHistoryItemInfo,
    public changeType?: string,
    public changeSpecifier?: string,
    public changeSpecifier2?: string,
    public changeTime?: Date,
    public fieldChanges?: Array<FieldChange>
  ) {
    this.fieldChanges = fieldChanges || [];
  }

  get uiChangeTime(): string {
    return TimeUtil.getUiDateTimeString(this.changeTime);
  }
}
