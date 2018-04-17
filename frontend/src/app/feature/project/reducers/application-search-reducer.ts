import {Application} from '../../../model/application/application';
import {SearchActions, SearchActionType} from '../actions/application-search-actions';

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
    case SearchActionType.Search: {
      return {
        ...state,
        term: action.payload,
        searching: true,
        matchingApplications: []
      };
    }

    case SearchActionType.SearchSuccess: {
      return {
        ...state,
        matchingApplications: action.payload,
        searching: false
      };
    }

    case SearchActionType.SearchFailed: {
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
