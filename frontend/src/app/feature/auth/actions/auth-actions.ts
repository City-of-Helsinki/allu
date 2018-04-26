import {Action} from '@ngrx/store';
import {User} from '../../../model/user/user';

export enum AuthActionType {
  LoggedIn = '[AuthAction] User logged in',
  LoggedOut = '[AuthAction] User logged out',
  LoggedUserLoaded = '[AuthAction] Loaded user who logged in',
}

export class LoggedIn implements Action {
  readonly type = AuthActionType.LoggedIn;
}

export class LoggedOut implements Action {
  readonly type = AuthActionType.LoggedOut;
}

export class LoggedUserLoaded implements Action {
  readonly type = AuthActionType.LoggedUserLoaded;

  constructor(public payload: User) {}
}

export type AuthActions =
  | LoggedIn
  | LoggedOut
  | LoggedUserLoaded;
