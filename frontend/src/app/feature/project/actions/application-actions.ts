import {Action} from '@ngrx/store';
import {Application} from '../../../model/application/application';
import {Sort} from '../../../model/common/sort';
import {PageRequest} from '../../../model/common/page-request';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/ui-state/error-info';

export enum ApplicationActionTypes {
  Load = '[Project] Load applications',
  LoadSuccess = '[Project] Load applications success',
  LoadFailed = '[Project] Load applications failed',
  Paged = '[Project] Get paged applications',
  Add = '[Project] Add application',
  AddSuccess = '[Project] Add application success',
  AddFailed = '[Project] Add application failed',
  Remove = '[Project] Remove application',
  RemoveSuccess = '[Project] Remove application success',
  RemoveFailed = '[Project] Remove application failed'
}

export class Load implements Action {
  readonly type = ApplicationActionTypes.Load;
}

export class LoadSuccess implements Action {
  readonly type = ApplicationActionTypes.LoadSuccess;

  constructor(public payload: Application[]) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ApplicationActionTypes.LoadFailed;

  constructor(public payload: ErrorInfo) {}
}

export class Paged implements Action {
  readonly type = ApplicationActionTypes.Paged;
  public payload: { sort?: Sort, pageRequest?: PageRequest };

  constructor(public sort?: Sort, public pageRequest?: PageRequest) {
    this.payload = {sort, pageRequest};
  }
}

export class Add implements Action {
  readonly type = ApplicationActionTypes.Add;

  constructor(public payload: number) {}
}

export class AddSuccess implements Action {
  readonly type = ApplicationActionTypes.AddSuccess;

  constructor(public payload: Application[]) {}
}

export class AddFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ApplicationActionTypes.AddFailed;

  constructor(public payload: ErrorInfo) {}
}

export class Remove implements Action {
  readonly type = ApplicationActionTypes.Remove;

  constructor(public payload: number) {}
}

export class RemoveSuccess implements Action {
  readonly type = ApplicationActionTypes.RemoveSuccess;

  constructor(public payload: number) {}
}

export class RemoveFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ApplicationActionTypes.RemoveFailed;

  constructor(public payload: ErrorInfo) {}
}


export type ApplicationActions =
  | Load
  | LoadSuccess
  | LoadFailed
  | Paged
  | Add
  | AddSuccess
  | AddFailed
  | Remove
  | RemoveSuccess
  | RemoveFailed;

