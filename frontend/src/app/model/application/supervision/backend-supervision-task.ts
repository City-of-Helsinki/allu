import {BackendUser} from '../../../service/backend-model/backend-user';
import {SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {SupervisionTaskStatusType} from '@model/application/supervision/supervision-task-status-type';
import {BackendSupervisionTaskLocation} from '@app/service/backend-model/backend-location';

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
  locationId: number;
  approvedLocations: BackendSupervisionTaskLocation[];
}
