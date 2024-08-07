import {BackendFieldChange} from './backend-field-change';
import {BackendUser} from './backend-user';
import {BackendChangeHistoryItemInfo} from './backend-change-history-item-info';

export interface BackendChangeHistoryItem {
  user: BackendUser;
  info: BackendChangeHistoryItemInfo;
  changeType: string;
  changeSpecifier: string;
  changeSpecifier2?: string;
  changeTime: string;
  fieldChanges: Array<BackendFieldChange>;
}
