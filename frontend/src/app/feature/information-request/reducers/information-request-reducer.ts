import {InformationRequestResponse} from '@model/information-request/information-request-response';
import {InformationRequestAction, InformationRequestActionType} from '../actions/information-request-actions';
import {InformationRequest} from '@model/information-request/information-request';
import {NumberUtil} from '@util/number.util';

export interface State {
  request: InformationRequest;
  requestLoading: boolean;
  response: InformationRequestResponse;
  responseLoading: boolean;
}

export const initialState: State = {
  request: undefined,
  requestLoading: false,
  response: undefined,
  responseLoading: false
};

export function reducer(state: State = initialState, action: InformationRequestAction) {
  switch (action.type) {
    case InformationRequestActionType.LoadLatestRequest:
    case InformationRequestActionType.LoadLatestRequestFailed: {
      return {
        ...state,
        requestLoading: true,
        request: undefined
      };
    }

    case InformationRequestActionType.LoadLatestRequestSuccess: {
      return {
        ...state,
        request: action.payload,
        requestLoading: false
      };
    }

    case InformationRequestActionType.SaveRequestSuccess: {
      return {
        ...state,
        request: action.payload
      };
    }

    case InformationRequestActionType.LoadLatestResponse:
    case InformationRequestActionType.LoadLatestResponseFailed: {
      return {
        ...state,
        responseLoading: true,
        response: undefined
      };
    }

    case InformationRequestActionType.LoadLatestResponseSuccess: {
      return {
        ...state,
        response: action.payload,
        responseLoading: false
      };
    }

    case InformationRequestActionType.CancelRequestSuccess: {
      return {
        ...initialState
      };
    }

    default: {
      return {
        ...state
      };
    }
  }
}

export const getRequest = (state: State) => state.request;

export const getRequestLoading = (state: State) => state.requestLoading;

export const getResponse = (state: State) => state.response;

export const getResponseLoading = (state: State) => state.responseLoading;

export const getResponsePending = (state: State) => state.request ? NumberUtil.isDefined(state.request.informationRequestId) : false;
