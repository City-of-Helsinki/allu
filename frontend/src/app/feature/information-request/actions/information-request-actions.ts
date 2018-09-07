import {Action} from '@ngrx/store';
import {InformationRequestResponse} from '../../../model/information-request/information-request-response';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/error/error-info';
import {InformationRequest} from '@model/information-request/information-request';

export enum InformationRequestActionType {
  LoadLatestRequest = '[InformationRequest] Load latest information request',
  LoadLatestRequestSuccess = '[InformationRequest] Load latest information request success',
  LoadLatestRequestFailed = '[InformationRequest] Load latest information request failed',
  SaveRequest = '[InformationRequest] Save information request',
  SaveAndSendRequest = '[InformationRequest] Save and send information request',
  SaveRequestSuccess = '[InformationRequest] Save information request success',
  SaveRequestFailed = '[InformationRequest] Save information request failed',
  LoadLatestResponse = '[InformationRequest] Load latest information request response',
  LoadLatestResponseSuccess= '[InformationRequest] Load latest information request response success',
  LoadLatestResponseFailed = '[InformationRequest] Load latest information request response failed'
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

export class SaveRequestFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = InformationRequestActionType.SaveRequestFailed;
  constructor(public payload: ErrorInfo) {}
}

export class LoadLatestResponse implements Action {
  readonly type = InformationRequestActionType.LoadLatestResponse;
}

export class LoadLatestResponseSuccess implements Action {
  readonly type = InformationRequestActionType.LoadLatestResponseSuccess;
  constructor(public payload: InformationRequestResponse) {}
}

export class LoadLatestResponseFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = InformationRequestActionType.LoadLatestResponseFailed;
  constructor(public payload: ErrorInfo) {}
}

export type InformationRequestAction =
  | LoadLatestRequest
  | LoadLatestRequestSuccess
  | LoadLatestRequestFailed
  | SaveRequest
  | SaveRequestSuccess
  | SaveRequestFailed
  | LoadLatestResponse
  | LoadLatestResponseSuccess
  | LoadLatestResponseFailed;
