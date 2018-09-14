import {BackendUser} from '../../../service/backend-model/backend-user';
import {SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {SupervisionTaskStatusType} from '@model/application/supervision/supervision-task-status-type';

export interface BackendSupervisionTask {
  id: number;
  applicationId: number;
  type: SupervisionTaskType;
  creator: BackendUser;
  owner: BackendUser;
  creationTime: string;
  plannedFinishingTime: string;
  actualFinishingTime: string;
  status: SupervisionTaskStatusType;
  description: string;
  result: string;
}
