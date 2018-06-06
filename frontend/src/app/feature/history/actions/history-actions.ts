import {ActionWithTarget} from '../../allu/actions/action-with-target';
import {ActionTargetType} from '../../allu/actions/action-target-type';
import {ChangeHistoryItem} from '../../../model/history/change-history-item';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/error/error-info';

export enum HistoryActionType {
  Load = '[History] Load history',
  LoadSuccess = '[History] Load history success',
  LoadFailed = '[History] Load history failed',
  SetFieldsVisible = '[History] Set fields visible'
}

export class Load implements ActionWithTarget {
  readonly type = HistoryActionType.Load;

  constructor(public targetType: ActionTargetType) {}
}

export class LoadSuccess implements ActionWithTarget {
  readonly type = HistoryActionType.LoadSuccess;

  constructor(public targetType: ActionTargetType, public payload: ChangeHistoryItem[]) {}
}

export class LoadFailed implements ActionWithTarget, ActionWithPayload<ErrorInfo> {
  readonly type = HistoryActionType.LoadFailed;

  constructor(public targetType: ActionTargetType, public payload: ErrorInfo) {}
}

export class SetFieldsVisible implements ActionWithTarget {
  readonly type = HistoryActionType.SetFieldsVisible;

  constructor(public targetType: ActionTargetType, public payload: boolean) {}
}

export type HistoryActions =
  | Load
  | LoadSuccess
  | LoadFailed
  | SetFieldsVisible;
