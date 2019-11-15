import {Action} from '@ngrx/store';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';
import {InformationRequest} from '@model/information-request/information-request';

export enum InformationRequestActionType {
  LoadRequest = '[InformationRequest] Load information request',
  LoadActiveRequest = '[InformationRequest] Load active information request',
  LoadRequestSuccess = '[InformationRequest] Load information request success',
  LoadRequestFailed = '[InformationRequest] Load information request failed',
  SaveRequest = '[InformationRequest] Save information request',
  SaveAndSendRequest = '[InformationRequest] Save and send information request',
  SaveRequestSuccess = '[InformationRequest] Save information request success',
  CancelRequest = '[InformationRequest] Cancel information request',
  CancelRequestSuccess = '[InformationRequest] Cancel information request success',
  CloseRequest = '[InformationRequest] Close information request'
}

export class LoadRequest implements Action {
  readonly type = InformationRequestActionType.LoadRequest;
  constructor(public payload: number) {}
}

export class LoadActiveRequest implements Action {
  readonly type = InformationRequestActionType.LoadActiveRequest;
}

export class LoadRequestSuccess implements Action {
  readonly type = InformationRequestActionType.LoadRequestSuccess;
  constructor(public payload: InformationRequest) {}
}

export class LoadRequestFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = InformationRequestActionType.LoadRequestFailed;
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
  constructor(public payload: number) {}
}

export class CancelRequestSuccess implements Action {
  readonly type = InformationRequestActionType.CancelRequestSuccess;
  constructor(public payload: number) {}
}

export class CloseRequest implements Action {
  readonly type = InformationRequestActionType.CloseRequest;
  constructor(public payload: number) {}
}

export type InformationRequestAction =
  | LoadRequest
  | LoadActiveRequest
  | LoadRequestSuccess
  | LoadRequestFailed
  | SaveRequest
  | SaveRequestSuccess
  | CancelRequest
  | CancelRequestSuccess
  | CloseRequest;
