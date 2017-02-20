import {BackendApplicationFieldChange} from './backend-application-field-change';

export interface BackendApplicationChange {
  userId: number;
  changeType: string;
  newStatus: string;
  changeTime: string;
  fieldChanges: Array<BackendApplicationFieldChange>;
}
