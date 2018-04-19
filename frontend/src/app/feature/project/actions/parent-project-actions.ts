import {Action} from '@ngrx/store';
import {Project} from '../../../model/project/project';
import {ErrorInfo} from '../../../service/ui-state/error-info';
import {ActionWithPayload} from '../../common/action-with-payload';

export enum ParentProjectActionType {
  Load = '[ParentProject] Load project parents',
  LoadSuccess = '[ParentProject] Load project parents success',
  LoadFailed = '[ParentProject] Load project parents failed'
}

export class Load implements Action {
  readonly type = ParentProjectActionType.Load;
}

export class LoadSuccess implements Action {
  readonly type = ParentProjectActionType.LoadSuccess;

  constructor(public payload: Project[]) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ParentProjectActionType.LoadFailed;

  constructor(public payload: ErrorInfo) {}
}

export type ParentProjectActions =
  | Load
  | LoadSuccess
  | LoadFailed;
