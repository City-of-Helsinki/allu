import {Action} from '@ngrx/store';
import {Project} from '../../../model/project/project';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/ui-state/error-info';

export enum ProjectActionTypes {
  Load = '[Project] Load project',
  LoadSuccess = '[Project] Load project success',
  LoadFailed = '[Project] Load project failed',
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

export type ProjectActions =
  | Load
  | LoadSuccess
  | LoadFailed;
