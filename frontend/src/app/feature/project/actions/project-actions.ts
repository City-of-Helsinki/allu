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

export type ProjectActions =
  | Load
  | LoadSuccess
  | LoadFailed
  | Save
  | SaveSuccess
  | SaveFailed;
