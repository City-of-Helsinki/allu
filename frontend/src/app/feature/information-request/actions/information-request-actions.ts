import {Action} from '@ngrx/store';
import {InformationRequestResponse} from '../../../model/information-request/information-request-response';
import {ActionWithPayload} from '../../common/action-with-payload';
import {ErrorInfo} from '../../../service/error/error-info';

export enum InformationRequestActionType {
  LoadLatestResponse = '[InformationRequest] Load latest information request response',
  LoadLatestResponseSuccess= '[InformationRequest] Load latest information request response success',
  LoadLatestResponseFailed = '[InformationRequest] Load latest information request response failed'
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
  | LoadLatestResponse
  | LoadLatestResponseSuccess
  | LoadLatestResponseFailed;
