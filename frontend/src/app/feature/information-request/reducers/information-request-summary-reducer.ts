import {InformationRequestSummary} from '@model/information-request/information-request-summary';
import {
  InformationRequestSummaryAction,
  InformationRequestSummaryActionType
} from '@feature/information-request/actions/information-request-summary-actions';

export interface State {
  summaries: InformationRequestSummary[];
  loading: boolean;
  loaded: boolean;
}

export const initialState: State = {
  summaries: [],
  loading: false,
  loaded: false
};

export function reducer(state: State = initialState, action: InformationRequestSummaryAction) {
  switch (action.type) {
    case InformationRequestSummaryActionType.Load: {
      return {
        ...state,
        loading: true,
        loaded: false
      };
    }

    case InformationRequestSummaryActionType.LoadSuccess: {
      return {
        ...state,
        summaries: action.payload,
        loading: false,
        loaded: true
      };
    }

    case InformationRequestSummaryActionType.MarkForReload: {
      return {
        ...state,
        loaded: false
      };
    }

    default: {
      return state;
    }
  }
}

export const getSummaries = (state: State) => state.summaries;

export const getLoading = (state: State) => state.loading;

export const getLoaded = (state: State) => state.loaded;
