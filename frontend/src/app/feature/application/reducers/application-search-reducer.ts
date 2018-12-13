import {Application} from '@model/application/application';
import {ApplicationSearchActions, ApplicationSearchActionType} from '../actions/application-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

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

export function reducer(state: State = initialState, action: ApplicationSearchActions) {
  switch (action.type) {
    case ApplicationSearchActionType.SearchByNameOrId: {
      return {
        ...state,
        term: action.payload,
        searching: true,
        matchingApplications: []
      };
    }

    case ApplicationSearchActionType.SearchByNameOrIdSuccess: {
      return {
        ...state,
        matchingApplications: action.payload,
        searching: false
      };
    }

    case ApplicationSearchActionType.SearchByNameOrIdFailed: {
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

export function createReducerFor(targetType: ActionTargetType) {
  return function(state: State = initialState, action: ApplicationSearchActions) {
    if (targetType === action.targetType) {
      return reducer(state, action);
    } else {
      return state;
    }
  };
}

export const getMatchingApplications = (state: State) => state.matchingApplications;
