import {Action} from '@ngrx/store';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';
import {Contract} from '@model/contract/contract';
import {ContractApprovalInfo} from '@model/decision/contract-approval-info';

export enum ContractActionType {
  Load = '[Contract] Load contract',
  LoadSuccess = '[Contract] Load contract success',
  LoadFailed = '[Contract] Load contract failed',
  CreateProposal = '[Contract] Create contract proposal',
  CreateProposalSuccess = '[Contract] Create contract proposal success',
  CreateProposalFailed = '[Contract] Create contract proposal failed',
  Approve = '[Contract] Approve contract',
  ApproveSuccess = '[Contract] Approve contract success',
  ApproveFailed = '[Contract] Approve contract failed',
  Reject = '[Contract] Reject contract',
  RejectSuccess = '[Contract] Reject contract success'
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

export class CreateProposal implements Action {
  readonly type = ContractActionType.CreateProposal;
}

export class CreateProposalSuccess implements Action {
  readonly type = ContractActionType.CreateProposalSuccess;
  constructor(public payload: Contract) {}
}

export class CreateProposalFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ContractActionType.CreateProposalFailed;
  constructor(public payload: ErrorInfo) {}
}

export class Approve implements Action {
  readonly type = ContractActionType.Approve;
  constructor(public payload: ContractApprovalInfo) {}
}

export class ApproveSuccess implements Action {
  readonly type = ContractActionType.ApproveSuccess;
  constructor(public payload: Contract) {}
}

export class ApproveFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = ContractActionType.ApproveFailed;
  constructor(public payload: ErrorInfo) {}
}

export class Reject implements Action {
  readonly type = ContractActionType.Reject;
  constructor(public payload: string) {}
}

export class RejectSuccess implements Action {
  readonly type = ContractActionType.RejectSuccess;
}

export type ContractActions =
  | Load
  | LoadSuccess
  | LoadFailed
  | CreateProposal
  | CreateProposalSuccess
  | CreateProposalFailed
  | Approve
  | ApproveSuccess
  | ApproveFailed
  | Reject
  | RejectSuccess;
