import {InformationRequestResponse} from '../../../model/information-request/information-request-response';
import {InformationRequestAction, InformationRequestActionType} from '../actions/information-request-actions';

export interface State {
  response: InformationRequestResponse;
  responseLoading: boolean;
}

export const initialState: State = {
  response: undefined,
  responseLoading: false
};

export function reducer(state: State = initialState, action: InformationRequestAction) {
  switch (action.type) {
    case InformationRequestActionType.LoadLatestResponse: {
      return {
        ...state,
        responseLoading: true
      };
    }

    case InformationRequestActionType.LoadLatestResponseSuccess: {
      return {
        ...state,
        response: action.payload,
        responseLoading: false
      };
    }

    case InformationRequestActionType.LoadLatestResponseFailed: {
      return {
        ...state,
        responseLoading: false
      };
    }

    default: {
      return {
        ...state
      };
    }
  }
}

export const getResponse = (state: State) => state.response;

export const getResponseLoading = (state: State) => state.responseLoading;
