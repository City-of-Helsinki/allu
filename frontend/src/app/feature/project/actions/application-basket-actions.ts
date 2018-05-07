import {Action} from '@ngrx/store';
import {Application} from '../../../model/application/application';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/error/error-info';

export enum ApplicationBasketActionType {
  Load = '[ApplicationBasket] Load basket',
  LoadSuccess = '[ApplicationBasket] Load basket success',
  LoadFailed = '[ApplicationBasket] Load basket failed',
  Clear = '[ApplicationBasket] Clear basket',
  Add = '[ApplicationBasket] Add application',
  AddMultiple = '[ApplicationBasket] Add multiple application',
  Remove = '[ApplicationBasket] Remove application',
  CreateProject = '[ApplicationBasket] Create project with baskets content'
}

export class Load implements Action {
  readonly type = ApplicationBasketActionType.Load;

  constructor(public payload: number[]) {}
}

export class LoadSuccess implements Action {
  readonly type = ApplicationBasketActionType.LoadSuccess;

  constructor(public payload: Application[]) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ApplicationBasketActionType.LoadFailed;

  constructor(public payload: ErrorInfo) {}
}

export class Clear implements Action {
  readonly type = ApplicationBasketActionType.Clear;
}

export class Add implements Action {
  readonly type = ApplicationBasketActionType.Add;

  constructor(public payload: number) {}
}

export class AddMultiple implements Action {
  readonly type = ApplicationBasketActionType.AddMultiple;

  constructor(public payload: number[]) {}
}

export class Remove implements Action {
  readonly type = ApplicationBasketActionType.Remove;

  constructor(public payload: number) {}
}

export class CreateProject implements Action {
  readonly type = ApplicationBasketActionType.CreateProject;
}

export type ApplicationBasketActions =
  | Load
  | LoadSuccess
  | LoadFailed
  | Clear
  | Add
  | AddMultiple
  | Remove
  | CreateProject;


