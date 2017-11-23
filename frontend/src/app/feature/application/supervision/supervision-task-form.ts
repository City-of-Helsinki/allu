import {SupervisionTask} from '../../../model/application/supervision/supervision-task';
import {Some} from '../../../util/option';
import {User} from '../../../model/user/user';

export class SupervisionTaskForm {
  constructor(
    public id?: number,
    public applicationId?: number,
    public type?: string,
    public creatorId?: number,
    public creatorName?: string,
    public handlerId?: number,
    public handlerName?: string,
    public creationTime?: Date,
    public plannedFinishingTime?: Date,
    public actualFinishingTime?: Date,
    public status?: string,
    public description?: string,
    public result?: string
  ) {}

  static from(task: SupervisionTask): SupervisionTaskForm {
    const form = new SupervisionTaskForm();
    form.id = task.id;
    form.applicationId = task.applicationId;
    form.type = task.uiType;
    Some(task.creator).do(creator => {
      form.creatorId = creator.id;
      form.creatorName = creator.realName;
    });
    Some(task.handler).do(handler => {
      form.handlerId = handler.id;
      form.handlerName = handler.realName;
    });
    form.creationTime = task.creationTime;
    form.plannedFinishingTime = task.plannedFinishingTime;
    form.actualFinishingTime = task.actualFinishingTime;
    form.status = task.uiStatus;
    form.description = task.description;
    form.result = task.result;
    return form;
  }

  static to(form: SupervisionTaskForm): SupervisionTask {
    const task = new SupervisionTask();
    task.id = form.id;
    task.applicationId = form.applicationId;
    task.uiType = form.type;
    task.creator = Some(form.creatorId).map(id => new User(id)).orElse(undefined);
    task.handler = Some(form.handlerId).map(id => new User(id)).orElse(undefined);
    task.creationTime = form.creationTime;
    task.plannedFinishingTime = form.plannedFinishingTime;
    task.actualFinishingTime = form.actualFinishingTime;
    task.uiStatus = form.status;
    task.description = form.description;
    task.result = form.result;
    return task;
  }
}
