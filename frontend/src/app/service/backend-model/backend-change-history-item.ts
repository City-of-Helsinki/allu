import {BackendFieldChange} from './backend-field-change';
import {BackendUser} from './backend-user';

export interface BackendChangeHistoryItem {
  user: BackendUser;
  changeType: string;
  newStatus: string;
  changeTime: string;
  fieldChanges: Array<BackendFieldChange>;
}
