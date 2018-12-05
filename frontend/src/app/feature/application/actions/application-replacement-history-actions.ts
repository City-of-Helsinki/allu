import {Action} from '@ngrx/store';
import {ApplicationIdentifier} from '@model/application/application-identifier';

export enum ApplicationReplacementHistoryActionType {
  Load = '[ApplicationReplacementHistory] Load replacement history',
  LoadSuccess = '[ApplicationReplacementHistory] Load replacement history success'
}

export class Load implements Action {
  readonly type = ApplicationReplacementHistoryActionType.Load;
}

export class LoadSuccess implements Action {
  readonly type = ApplicationReplacementHistoryActionType.LoadSuccess;

  constructor(public payload: ApplicationIdentifier[]) {}
}

export type ApplicationReplacementHistoryActions =
  | Load
  | LoadSuccess;
