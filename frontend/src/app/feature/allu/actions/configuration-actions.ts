import {Action} from '@ngrx/store';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';
import {ConfigurationKeyMap} from '@model/config/configuration';

export enum ConfigurationActionType {
  LoadSuccess = '[Configuration] Load code set success',
  LoadFailed = '[Configuration] Load code set failed',
}

export class LoadSuccess implements Action {
  readonly type = ConfigurationActionType.LoadSuccess;

  constructor(public payload: ConfigurationKeyMap) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ConfigurationActionType.LoadFailed;

  constructor(public payload: ErrorInfo) {}
}

export type ConfigurationActions =
  | LoadSuccess
  | LoadFailed;
