import {SupervisionTask} from '@model/application/supervision/supervision-task';
import {isAutomaticSupervisionTaskType, SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {Some} from '@util/option';
import {User} from '@model/user/user';
import {SupervisionTaskStatusType} from '@model/application/supervision/supervision-task-status-type';

export class SupervisionTaskForm {
  constructor(
    public id?: number,
    public applicationId?: number,
    public type?: SupervisionTaskType,
    public creatorId?: number,
    public creatorName?: string,
    public ownerId?: number,
    public ownerName?: string,
    public creationTime?: Date,
    public plannedFinishingTime?: Date,
    public actualFinishingTime?: Date,
    public status?: SupervisionTaskStatusType,
    public description?: string,
    public result?: string,
    public automatic?: boolean
  ) {}

  static from(task: SupervisionTask): SupervisionTaskForm {
    const form = new SupervisionTaskForm();
    form.id = task.id;
    form.applicationId = task.applicationId;
    form.type = task.type;
    Some(task.creator).do(creator => {
      form.creatorId = creator.id;
      form.creatorName = creator.realName;
    });
    Some(task.owner).do(owner => {
      form.ownerId = owner.id;
      form.ownerName = owner.realName;
    });
    form.creationTime = task.creationTime;
    form.plannedFinishingTime = task.plannedFinishingTime;
    form.actualFinishingTime = task.actualFinishingTime;
    form.status = task.status;
    form.description = task.description;
    form.result = task.result;
    form.automatic = isAutomaticSupervisionTaskType(task.type);
    return form;
  }

  static to(form: SupervisionTaskForm): SupervisionTask {
    const task = new SupervisionTask();
    task.id = form.id;
    task.applicationId = form.applicationId;
    task.type = form.type;
    task.creator = Some(form.creatorId).map(id => new User(id)).orElse(undefined);
    task.owner = Some(form.ownerId).map(id => new User(id)).orElse(undefined);
    task.creationTime = form.creationTime;
    task.plannedFinishingTime = form.plannedFinishingTime;
    task.actualFinishingTime = form.actualFinishingTime;
    task.status = form.status;
    task.description = form.description;
    task.result = form.result;
    return task;
  }
}
