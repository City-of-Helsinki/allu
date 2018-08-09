import {Action} from '@ngrx/store';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';
import {Contract} from '@model/contract/contract';

export enum ContractActionType {
  Load = '[Contract] Load contract',
  LoadSuccess = '[Contract] Load contract success',
  LoadFailed = '[Contract] Load contract failed',
}

export class Load implements Action {
  readonly type = ContractActionType.Load;
}

export class LoadSuccess implements Action {
  readonly type = ContractActionType.LoadSuccess;
  constructor(public payload: Contract) {}
}

export class LoadFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ContractActionType.LoadFailed;
  constructor(public payload: ErrorInfo) {}
}

export type ContractActions =
  | Load
  | LoadSuccess
  | LoadFailed;
