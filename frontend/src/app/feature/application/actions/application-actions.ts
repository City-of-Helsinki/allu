import {Action} from '@ngrx/store';
import {ErrorInfo} from '@service/error/error-info';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {Application} from '@model/application/application';
import {ApplicationType} from '@model/application/type/application-type';
import {KindsWithSpecifiers} from '@model/application/type/application-specifier';

export enum ApplicationActionType {
  Load = '[Application] Load application',
  LoadSuccess = '[Application] Load application success',
  LoadFailed = '[Application] Load application failed',
  SetType = '[Application] Set type',
  SetKindsWithSpecifiers = '[Application] Set kinds with specifiers',
  RemoveClientApplicationData = '[Application] Remove client application data'
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

export class SetType implements Action {
  readonly type = ApplicationActionType.SetType;

  constructor(public payload: ApplicationType) {}
}

export class SetKindsWithSpecifiers implements Action {
  readonly type = ApplicationActionType.SetKindsWithSpecifiers;

  constructor(public payload: KindsWithSpecifiers) {}
}

export class RemoveClientApplicationData implements Action  {
  readonly type = ApplicationActionType.RemoveClientApplicationData;
}

export type ApplicationActions =
  | Load
  | LoadSuccess
  | LoadFailed
  | SetType
  | SetKindsWithSpecifiers
  | RemoveClientApplicationData;
