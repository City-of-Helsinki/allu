import {BulkApprovalEntry} from '@app/model/decision/bulk-approval-entry';
import {ErrorInfo} from '@app/service/error/error-info';
import {Action} from '@ngrx/store';

export enum BulkApprovalActionType {
  Load = '[BulkApproval] Load bulk approval entries',
  LoadComplete = '[BulkApproval] Load bulk approval entries complete'
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

export type BulkApprovalActions =
  | Load
  | LoadComplete;
