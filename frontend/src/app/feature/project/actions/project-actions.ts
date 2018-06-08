import {Action} from '@ngrx/store';
import {Project} from '../../../model/project/project';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/error/error-info';

export enum ProjectActionTypes {
  Load = '[Project] Load project',
  LoadSuccess = '[Project] Load project success',
  LoadFailed = '[Project] Load project failed',
  Save = '[Project] Save project',
  SaveSuccess = '[Project] Save project success',
  SaveFailed = '[Project] Save project failed',
  Delete = '[Project] Delete project',
  DeleteSuccess = '[Project] Delete project success',
  DeleteFailed = '[Project] Delete project failed',
  ShowBasicInfo = '[Project] Set show basic info',
  RemoveParent = '[Project] Remove projects parent',
  RemoveParentSuccess = '[Project] Remove projects parent success',
  RemoveParentFailed = '[Project] Remove projects parent failed'
}

export class Load implements Action {
  readonly type = ProjectActionTypes.Load;

  constructor(public payload: number) {}
}

export class LoadSuccess implements Action {
  readonly type = ProjectActionTypes.LoadSuccess;

  constructor(public payload: Project) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ProjectActionTypes.LoadFailed;

  constructor(public payload: ErrorInfo) {}
}

export class Save implements Action {
  readonly type = ProjectActionTypes.Save;

  constructor(public payload: Project) {}
}

export class SaveSuccess implements Action {
  readonly type = ProjectActionTypes.SaveSuccess;

  constructor(public payload: Project) {}
}

export class SaveFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ProjectActionTypes.SaveFailed;

  constructor(public payload: ErrorInfo) {}
}

export class Delete implements Action {
  readonly type = ProjectActionTypes.Delete;
}

export class DeleteSuccess implements Action {
  readonly type = ProjectActionTypes.DeleteSuccess;
}

export class DeleteFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ProjectActionTypes.DeleteFailed;

  constructor(public payload: ErrorInfo) {}
}

export class ShowBasicInfo implements Action {
  readonly type = ProjectActionTypes.ShowBasicInfo;

  constructor(public payload: boolean) {}
}

export class RemoveParent implements Action {
  readonly type = ProjectActionTypes.RemoveParent;

  constructor(public payload: number[]) {}
}

export class RemoveParentSuccess implements Action {
  readonly type = ProjectActionTypes.RemoveParentSuccess;
}

export class RemoveParentFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ProjectActionTypes.RemoveParentFailed;

  constructor(public payload: ErrorInfo) {}
}

export type ProjectActions =
  | Load
  | LoadSuccess
  | LoadFailed
  | Save
  | SaveSuccess
  | SaveFailed
  | Delete
  | DeleteSuccess
  | DeleteFailed
  | ShowBasicInfo
  | RemoveParent
  | RemoveParentSuccess
  | RemoveParentFailed;
