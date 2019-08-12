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
  RemoveClientApplicationData = '[Application] Remove client application data',
  ChangeOwner = '[Application] Change owner for chosen applications',
  ChangeOwnerSuccess = '[Application] Change owner for chosen applications success',
  RemoveOwner = '[Application] Remove owner from chosen applications',
  RemoveOwnerSuccess = '[Application] Remove owner from chosen applications success',
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

export class ChangeOwner implements Action {
  readonly type = ApplicationActionType.ChangeOwner;
  readonly payload: {ownerId: number, applicationIds: number[]};

  constructor(public ownerId: number, public applicationIds: number[] = []) {
    this.payload = {ownerId, applicationIds};
  }
}

export class ChangeOwnerSuccess implements Action {
  readonly type = ApplicationActionType.ChangeOwnerSuccess;
}

export class RemoveOwner implements Action {
  readonly type = ApplicationActionType.RemoveOwner;

  constructor(public payload: number[]) {}
}

export class RemoveOwnerSuccess implements Action {
  readonly type = ApplicationActionType.RemoveOwnerSuccess;
}

export type ApplicationActions =
  | Load
  | LoadSuccess
  | LoadFailed
  | SetType
  | SetKindsWithSpecifiers
  | RemoveClientApplicationData
  | ChangeOwner
  | ChangeOwnerSuccess
  | RemoveOwner
  | RemoveOwnerSuccess;
