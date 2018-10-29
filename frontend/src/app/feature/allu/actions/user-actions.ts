import {Action} from '@ngrx/store';
import {User} from '@model/user/user';

export enum UserActionType {
  Load = '[User] Load users',
  LoadSuccess = '[User] Load users success'
}

export class Load implements Action {
  readonly type = UserActionType.Load;
}

export class LoadSuccess implements Action {
  readonly type = UserActionType.LoadSuccess;
  constructor(public payload: User[]) {}
}

export type UserActions =
  | Load
  | LoadSuccess;
