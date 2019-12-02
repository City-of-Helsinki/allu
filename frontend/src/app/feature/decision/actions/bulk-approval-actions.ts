import {BulkApprovalEntry} from '@app/model/decision/bulk-approval-entry';
import {ErrorInfo} from '@app/service/error/error-info';
import {Action} from '@ngrx/store';

export enum BulkApprovalActionType {
  Load = '[BulkApproval] Load bulk approval entries',
  LoadComplete = '[BulkApproval] Load bulk approval entries complete',
  Approve = '[BulkApproval] Approve entries in bulk',
  ApproveEntryComplete = '[BulkApproval] Approve single entry complete',
  ApproveComplete = '[BulkApproval] Approve entries in bulk complete'
}

export class Load implements Action {
  readonly type = BulkApprovalActionType.Load;
  constructor(public payload: number[]) {}
}

export class LoadComplete implements Action {
  readonly type = BulkApprovalActionType.LoadComplete;
  constructor(
    public payload: {
      entries: BulkApprovalEntry[];
      error?: ErrorInfo;
    }
  ) {}
}

export class Approve implements Action {
  readonly type = BulkApprovalActionType.Approve;
  constructor(public payload: BulkApprovalEntry[]) {}
}

export class ApproveEntryComplete implements Action {
  readonly type = BulkApprovalActionType.ApproveEntryComplete;
  constructor(
    public payload: {
      id: number,
      error?: ErrorInfo;
    }
  ) {}
}

export class ApproveComplete implements Action {
  readonly type = BulkApprovalActionType.ApproveComplete;
  constructor() {}
}

export type BulkApprovalActions =
  | Load
  | LoadComplete
  | Approve
  | ApproveEntryComplete
  | ApproveComplete;
