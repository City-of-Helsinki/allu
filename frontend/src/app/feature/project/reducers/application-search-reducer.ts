import {Application} from '../../../model/application/application';
import {SearchActions, ApplicationSearchActionType} from '../actions/application-search-actions';

export interface State {
  term: string;
  searching: boolean;
  matchingApplications: Application[];
}

const initialState: State = {
  term: undefined,
  searching: false,
  matchingApplications: []
};

export function reducer(state: State = initialState, action: SearchActions) {
  switch (action.type) {
    case ApplicationSearchActionType.Search: {
      return {
        ...state,
        term: action.payload,
        searching: true,
        matchingApplications: []
      };
    }

    case ApplicationSearchActionType.SearchSuccess: {
      return {
        ...state,
        matchingApplications: action.payload,
        searching: false
      };
    }

    case ApplicationSearchActionType.SearchFailed: {
      return {
        ...state,
        matchingApplications: [],
        searching: false
      };
    }

    default:
      return {...state};
  }
}

export const getMatchingApplications = (state: State) => state.matchingApplications;
