import {Project} from '../../../model/project/project';
import {ProjectSearchActionType, SearchActions} from '../actions/project-search-actions';

export interface State {
  term: string;
  searching: boolean;
  matching: Project[];
}

const initialState: State = {
  term: undefined,
  searching: false,
  matching: []
};

export function reducer(state: State = initialState, action: SearchActions) {
  switch (action.type) {
    case ProjectSearchActionType.Search: {
      return {
        ...state,
        term: action.payload,
        searching: true,
        matching: []
      };
    }

    case ProjectSearchActionType.SearchSuccess: {
      return {
        ...state,
        matching: action.payload,
        searching: false
      };
    }

    case ProjectSearchActionType.SearchFailed: {
      return {
        ...state,
        matching: [],
        searching: false
      };
    }

    default:
      return {...state};
  }
}

export const getMatching = (state: State) => state.matching;
