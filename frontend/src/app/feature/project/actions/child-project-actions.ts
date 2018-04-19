import {Action} from '@ngrx/store';
import {Project} from '../../../model/project/project';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/ui-state/error-info';

export enum ChildProjectActionType {
  Load = '[ChildProject] Load project children',
  LoadSuccess = '[ChildProject] Load project children success',
  LoadFailed = '[ChildProject] Load project children failed'
}

export class Load implements Action {
  readonly type = ChildProjectActionType.Load;
}

export class LoadSuccess implements Action {
  readonly type = ChildProjectActionType.LoadSuccess;

  constructor(public payload: Project[]) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ChildProjectActionType.LoadFailed;

  constructor(public payload: ErrorInfo) {}
}

export type ChildProjectActions =
  | Load
  | LoadSuccess
  | LoadFailed;
