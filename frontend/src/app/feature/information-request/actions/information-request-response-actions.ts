import {Action} from '@ngrx/store';
import {InformationRequestResponse} from '@model/information-request/information-request-response';
import {ActionWithPayload} from '@feature/common/action-with-payload';
import {ErrorInfo} from '@service/error/error-info';

export enum InformationRequestResponseActionType {
  LoadResponse = '[InformationRequestResponse] Load information request response',
  LoadResponseSuccess= '[InformationRequestResponse] Load information request response success',
  LoadResponseFailed = '[InformationRequestResponse] Load information request response failed'
}

export class LoadResponse implements Action {
  readonly type = InformationRequestResponseActionType.LoadResponse;
  constructor(public payload: number) {}
}

export class LoadResponseSuccess implements Action {
  readonly type = InformationRequestResponseActionType.LoadResponseSuccess;
  constructor(public payload: InformationRequestResponse) {}
}

export class LoadResponseFailed implements ActionWithPayload<ErrorInfo> {
  readonly type = InformationRequestResponseActionType.LoadResponseFailed;
  constructor(public payload: ErrorInfo) {}
}

export type InformationRequestResponseAction =
  | LoadResponse
  | LoadResponseSuccess
  | LoadResponseFailed;
