import {Action} from '@ngrx/store';
import {Decision} from '@model/decision/Decision';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';

export enum DecisionActionType {
  Load = '[Decision] Load decision',
  LoadSuccess = '[Decision] Load decision success',
  LoadFailed = '[Decision] Load decision failed'
}

export class Load implements Action {
  readonly type = DecisionActionType.Load;
  constructor() {}
}

export class LoadSuccess implements Action {
  readonly type = DecisionActionType.LoadSuccess;
  constructor(public payload: Decision) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = DecisionActionType.LoadFailed;
  constructor(public payload: ErrorInfo) {}
}

export type DecisionActions =
  | Load
  | LoadSuccess
  | LoadFailed;
