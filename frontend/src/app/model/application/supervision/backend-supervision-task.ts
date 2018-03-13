import {BackendUser} from '../../../service/backend-model/backend-user';

export interface BackendSupervisionTask {
  id: number;
  applicationId: number;
  type: string;
  creator: BackendUser;
  owner: BackendUser;
  creationTime: string;
  plannedFinishingTime: string;
  actualFinishingTime: string;
  status: string;
  description: string;
  result: string;
}
