import {SupervisionTask} from '@model/application/supervision/supervision-task';
import {Action} from '@ngrx/store';
import {ActionWithPayload} from '@feature/common/action-with-payload';

export enum SupervisionTaskActionType {
  Load = '[SupervisionTask] Load tasks',
  LoadSuccess = '[SupervisionTask] Load tasks success',
  LoadFailed = '[SupervisionTask] Load tasks failed',
  Save = '[SupervisionTask] Save task',
  SaveSuccess = '[SupervisionTask] Save task success',
  Remove = '[SupervisionTask] Remove task',
  RemoveSuccess = '[SupervisionTask] Remove task success',
  Approve = '[SupervisionTask] Approve task',
  ApproveSuccess = '[SupervisionTask] Approve task success',
  Reject = '[SupervisionTask] Reject task',
  RejectSuccess = '[SupervisionTask] Reject task success',
  ChangeOwner = '[SupervisionTask] Change owner',
  ChangeOwnerSuccess = '[SupervisionTask] Change owner success',
}

export class Load implements Action {
  readonly type = SupervisionTaskActionType.Load;
}

export class LoadSuccess implements Action {
  readonly type = SupervisionTaskActionType.LoadSuccess;
  constructor(public payload: SupervisionTask[]) {}
}

export class LoadFailed implements Action {
  readonly type = SupervisionTaskActionType.LoadFailed;
}

export class Save implements Action {
  readonly type = SupervisionTaskActionType.Save;
  constructor(public payload: SupervisionTask) {}
}

export class SaveSuccess implements Action {
  readonly type = SupervisionTaskActionType.SaveSuccess;
  constructor(public payload: SupervisionTask) {}
}

export class Remove implements Action {
  readonly type = SupervisionTaskActionType.Remove;
  constructor(public payload: number) {}
}

export class RemoveSuccess implements Action {
  readonly type = SupervisionTaskActionType.RemoveSuccess;
  constructor(public payload: number) {}
}

export class Approve implements Action {
  readonly type = SupervisionTaskActionType.Approve;
  constructor(public payload: SupervisionTask) {}
}

export class ApproveSuccess implements Action {
  readonly type = SupervisionTaskActionType.ApproveSuccess;
  constructor(public payload: SupervisionTask) {}
}

export class Reject implements Action {
  public readonly payload: {
    task: SupervisionTask;
    newSupervisionDate: Date
  };

  readonly type = SupervisionTaskActionType.Reject;
  constructor(public task: SupervisionTask, newSupervisionDate: Date) {
    this.payload = {task, newSupervisionDate};
  }
}

export class RejectSuccess implements Action {
  readonly type = SupervisionTaskActionType.RejectSuccess;
  constructor(public payload: SupervisionTask) {}
}

export class ChangeOwner implements Action {
  readonly type = SupervisionTaskActionType.ChangeOwner;
  constructor(public payload: {
    ownerId: number;
    taskIds: number[]
  }) {}
}

export class ChangeOwnerSuccess implements Action {
  readonly type = SupervisionTaskActionType.ChangeOwnerSuccess;
  constructor(public payload: SupervisionTask) {}
}


export type SupervisionTaskActions =
  | Load
  | LoadSuccess
  | LoadFailed
  | Save
  | SaveSuccess
  | Remove
  | RemoveSuccess
  | Approve
  | ApproveSuccess
  | Reject
  | RejectSuccess
  | ChangeOwner
  | ChangeOwnerSuccess;
