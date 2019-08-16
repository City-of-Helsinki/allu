import {SupervisionTaskType} from './supervision-task-type';
import {SupervisionTaskStatusType} from './supervision-task-status-type';
import {User} from '../../user/user';
import {Location} from '@model/common/location';

export class SupervisionTask {
  constructor(
    public id?: number,
    public applicationId?: number,
    public type: SupervisionTaskType = SupervisionTaskType.PRELIMINARY_SUPERVISION,
    public creator?: User,
    public owner?: User,
    public creationTime?: Date,
    public plannedFinishingTime?: Date,
    public actualFinishingTime?: Date,
    public status: SupervisionTaskStatusType = SupervisionTaskStatusType.OPEN,
    public description?: string,
    public result?: string,
    public locationId?: number,
    public approvedLocations?: Location[]) {
  }
}
