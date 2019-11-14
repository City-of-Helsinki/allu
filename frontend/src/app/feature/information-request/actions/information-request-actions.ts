import {Action} from '@ngrx/store';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';
import {InformationRequest} from '@model/information-request/information-request';

export enum InformationRequestActionType {
  LoadLatestRequest = '[InformationRequest] Load latest information request',
  LoadLatestRequestSuccess = '[InformationRequest] Load latest information request success',
  LoadLatestRequestFailed = '[InformationRequest] Load latest information request failed',
  SaveRequest = '[InformationRequest] Save information request',
  SaveAndSendRequest = '[InformationRequest] Save and send information request',
  SaveRequestSuccess = '[InformationRequest] Save information request success',
  CancelRequest = '[InformationRequest] Cancel information request',
  CancelRequestSuccess = '[InformationRequest] Cancel information request success',
  CloseRequest = '[InformationRequest] Close information request'
}

export class LoadLatestRequest implements Action {
  readonly type = InformationRequestActionType.LoadLatestRequest;
}

export class LoadLatestRequestSuccess implements Action {
  readonly type = InformationRequestActionType.LoadLatestRequestSuccess;
  constructor(public payload: InformationRequest) {}
}

export class LoadLatestRequestFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = InformationRequestActionType.LoadLatestRequestFailed;
  constructor(public payload: ErrorInfo) {}
}

export class SaveRequest implements Action {
  readonly type = InformationRequestActionType.SaveRequest;
  constructor(public payload: InformationRequest) {}
}

export class SaveAndSendRequest implements Action {
  readonly type = InformationRequestActionType.SaveAndSendRequest;
  constructor(public payload: InformationRequest) {}
}

export class SaveRequestSuccess implements Action {
  readonly type = InformationRequestActionType.SaveRequestSuccess;
  constructor(public payload: InformationRequest) {}
}

export class CancelRequest implements Action {
  readonly type = InformationRequestActionType.CancelRequest;
  constructor(public paylod: number) {}
}

export class CancelRequestSuccess implements Action {
  readonly type = InformationRequestActionType.CancelRequestSuccess;
}

export class CloseRequest implements Action {
  readonly type = InformationRequestActionType.CloseRequest;
  constructor(public payload: number) {}
}

export type InformationRequestAction =
  | LoadLatestRequest
  | LoadLatestRequestSuccess
  | LoadLatestRequestFailed
  | SaveRequest
  | SaveRequestSuccess
  | CancelRequest
  | CancelRequestSuccess
  | CloseRequest;
