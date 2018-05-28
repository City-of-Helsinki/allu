import {Action} from '@ngrx/store';
import {ErrorInfo} from '../../../service/error/error-info';
import {ActionWithPayload} from '../../common/action-with-payload';
import {Application} from '../../../model/application/application';
import {ApplicationTag} from '../../../model/application/tag/application-tag';

export enum ApplicationActionType {
  Load = '[Application] Load application',
  LoadSuccess = '[Application] Load application success',
  LoadFailed = '[Application] Load application failed'
}

export class Load implements Action {
  readonly type = ApplicationActionType.Load;

  constructor(public payload: number) {}
}

export class LoadSuccess implements Action {
  readonly type = ApplicationActionType.LoadSuccess;

  constructor(public payload: Application) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ApplicationActionType.LoadFailed;

  constructor(public payload: ErrorInfo) {}
}

export type ApplicationActions =
  | Load
  | LoadSuccess
  | LoadFailed;
