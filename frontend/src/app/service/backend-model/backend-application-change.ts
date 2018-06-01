import {BackendApplicationFieldChange} from './backend-application-field-change';
import {BackendUser} from './backend-user';

export interface BackendApplicationChange {
  user: BackendUser;
  changeType: string;
  newStatus: string;
  changeTime: string;
  fieldChanges: Array<BackendApplicationFieldChange>;
}
