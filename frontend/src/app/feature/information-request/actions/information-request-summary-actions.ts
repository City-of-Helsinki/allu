import {Action} from '@ngrx/store';
import {InformationRequestSummary} from '@model/information-request/information-request-summary';

export enum InformationRequestSummaryActionType {
  Get = '[InformationRequestSummary] Get information request summaries',
  Load = '[InformationRequestSummary] Load latest information request summaries',
  LoadSuccess = '[InformationRequestSummary] Load latest information request summaries success',
  MarkForReload = '[InformationRequestSummary] Marks summaries to be reloaded'
}

export class Get implements Action {
  readonly type = InformationRequestSummaryActionType.Get;
}

export class Load implements Action {
  readonly type = InformationRequestSummaryActionType.Load;
}

export class LoadSuccess implements Action {
  readonly type = InformationRequestSummaryActionType.LoadSuccess;
  constructor(public payload: InformationRequestSummary[]) {}
}

export class MarkForReload implements Action {
  readonly type = InformationRequestSummaryActionType.MarkForReload;
}

export type InformationRequestSummaryAction =
  | Get
  | Load
  | LoadSuccess
  | MarkForReload;
