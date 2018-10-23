import {Action} from '@ngrx/store';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';
import {Configuration} from '@model/config/configuration';

export enum ConfigurationActionType {
  LoadSuccess = '[Configuration] Load configurations success',
  LoadFailed = '[Configuration] Load configurations failed'
}

export class LoadSuccess implements Action {
  readonly type = ConfigurationActionType.LoadSuccess;

  constructor(public payload: Configuration[]) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ConfigurationActionType.LoadFailed;

  constructor(public payload: ErrorInfo) {}
}

export type ConfigurationActions =
  | LoadSuccess
  | LoadFailed;
