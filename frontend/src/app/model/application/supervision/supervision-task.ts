import {SupervisionTaskType} from './supervision-task-type';
import {SupervisionTaskStatusType} from './supervision-task-status-type';
import {User} from '../../user/user';

export class SupervisionTask {
  constructor(
    public id?: number,
    public applicationId?: number,
    public type: SupervisionTaskType = SupervisionTaskType.PRELIMINARY_SUPERVISION,
    public creator?: User,
    public handler?: User,
    public creationTime?: Date,
    public plannedFinishingTime?: Date,
    public actualFinishingTime?: Date,
    public status: SupervisionTaskStatusType = SupervisionTaskStatusType.OPEN,
    public description?: string,
    public result?: string) {
  }

  get uiType(): string {
    return SupervisionTaskType[this.type];
  }

  set uiType(type: string) {
    this.type = SupervisionTaskType[type];
  }

  get uiStatus(): string {
    return SupervisionTaskStatusType[this.status];
  }

  set uiStatus(status: string) {
    this.status = SupervisionTaskStatusType[status];
  }
}
