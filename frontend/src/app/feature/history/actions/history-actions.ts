import {ActionWithTarget} from '../../allu/actions/action-with-target';
import {ActionTargetType} from '../../allu/actions/action-target-type';
import {ChangeHistoryItem} from '../../../model/history/change-history-item';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/error/error-info';
import {ApplicationStatus} from '@model/application/application-status';

export enum HistoryActionType {
  Load = '[History] Load history',
  LoadByTargetId = '[History] Load history for target',
  LoadSuccess = '[History] Load history success',
  LoadFailed = '[History] Load history failed',
  LoadStatus = '[History] Load status history',
  LoadStatusComplete = '[History] Load status history success',
  SetFieldsVisible = '[History] Set fields visible'
}

export class Load implements ActionWithTarget {
  readonly type = HistoryActionType.Load;

  constructor(public targetType: ActionTargetType) {}
}

export class LoadByTargetId implements ActionWithTarget {
  readonly type = HistoryActionType.LoadByTargetId;

  constructor(public targetType: ActionTargetType, public payload: number) {}
}

export class LoadSuccess implements ActionWithTarget {
  readonly type = HistoryActionType.LoadSuccess;

  constructor(public targetType: ActionTargetType, public payload: ChangeHistoryItem[]) {}
}

export class LoadFailed implements ActionWithTarget, ActionWithPayload<ErrorInfo> {
  readonly type = HistoryActionType.LoadFailed;

  constructor(public targetType: ActionTargetType, public payload: ErrorInfo) {}
}

export class LoadStatus implements ActionWithTarget {
  readonly type = HistoryActionType.LoadStatus;

  constructor(public targetType: ActionTargetType, public payload: number) {}
}

export class LoadStatusComplete implements ActionWithTarget {
  readonly type = HistoryActionType.LoadStatusComplete;

  constructor(public targetType: ActionTargetType, public payload: ApplicationStatus[], public error?: ErrorInfo) {}
}

export class SetFieldsVisible implements ActionWithTarget {
  readonly type = HistoryActionType.SetFieldsVisible;

  constructor(public targetType: ActionTargetType, public payload: boolean) {}
}

export type HistoryActions =
  | Load
  | LoadByTargetId
  | LoadSuccess
  | LoadFailed
  | LoadStatus
  | LoadStatusComplete
  | SetFieldsVisible;
