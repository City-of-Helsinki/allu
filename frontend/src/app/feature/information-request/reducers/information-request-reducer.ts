import {InformationRequestAction, InformationRequestActionType} from '../actions/information-request-actions';
import {InformationRequest} from '@model/information-request/information-request';
import {NumberUtil} from '@util/number.util';
import {InformationRequestStatus} from '@model/information-request/information-request-status';

export interface State {
  request: InformationRequest;
  requestLoading: boolean;
}

export const initialState: State = {
  request: undefined,
  requestLoading: false,
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

export const getResponsePending = (state: State) => state.request
  ? state.request.status === InformationRequestStatus.RESPONSE_RECEIVED
  : false;
