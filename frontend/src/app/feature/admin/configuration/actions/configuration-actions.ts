import {Action} from '@ngrx/store';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';
import {Configuration} from '@model/config/configuration';

export enum ConfigurationActionType {
  LoadSuccess = '[Configuration] Load configurations success',
  LoadFailed = '[Configuration] Load configurations failed',
  Save = '[Configuration] Save configuration',
  SaveSuccess = '[Configuration] Save configuration success'
}

export class LoadSuccess implements Action {
  readonly type = ConfigurationActionType.LoadSuccess;

  constructor(public payload: Configuration[]) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ConfigurationActionType.LoadFailed;

  constructor(public payload: ErrorInfo) {}
}

export class Save implements Action {
  readonly type = ConfigurationActionType.Save;
  constructor(public payload: Configuration) {}
}

export class SaveSuccess implements Action {
  readonly type = ConfigurationActionType.SaveSuccess;
  constructor(public payload: Configuration) {}
}

export type ConfigurationActions =
  | LoadSuccess
  | LoadFailed
  | Save
  | SaveSuccess;
