import {Action} from '@ngrx/store';
import {Application} from '../../../model/application/application';
import {Sort} from '../../../model/common/sort';
import {PageRequest} from '../../../model/common/page-request';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/error/error-info';

export enum ApplicationActionTypes {
  Load = '[Project] Load applications',
  LoadSuccess = '[Project] Load applications success',
  LoadFailed = '[Project] Load applications failed',
  Paged = '[Project] Get paged applications',
  Add = '[Project] Add application',
  AddMultiple = '[Project] Add multiple applications',
  AddPending = '[Project] Add pending applications',
  AddSuccess = '[Project] Add application success',
  Remove = '[Project] Remove application',
  RemoveSuccess = '[Project] Remove application success'
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
  public payload: {sort?: Sort, pageRequest?: PageRequest };

  constructor(public sort?: Sort, public pageRequest?: PageRequest) {
    this.payload = {sort, pageRequest};
  }
}

export class Add implements Action {
  readonly type = ApplicationActionTypes.Add;

  constructor(public payload: number) {}
}

export class AddMultiple implements Action {
  readonly type = ApplicationActionTypes.AddMultiple;

  constructor(public payload: number[]) {}
}

export class AddPending implements Action {
  readonly type = ApplicationActionTypes.AddPending;

  constructor(public payload: number[]) {}
}

export class AddSuccess implements Action {
  readonly type = ApplicationActionTypes.AddSuccess;

  constructor(public payload: Application[]) {}
}

export class Remove implements Action {
  readonly type = ApplicationActionTypes.Remove;

  constructor(public payload: number) {}
}

export class RemoveSuccess implements Action {
  readonly type = ApplicationActionTypes.RemoveSuccess;

  constructor(public payload: number) {}
}


export type ApplicationActions =
  | Load
  | LoadSuccess
  | LoadFailed
  | Paged
  | Add
  | AddMultiple
  | AddPending
  | AddSuccess
  | Remove
  | RemoveSuccess;

