import {CustomerSearchByType, CustomerSearchQuery} from '../../../service/customer/customer-search-query';
import {Customer} from '../../../model/customer/customer';
import {CustomerSearchActions, CustomerSearchActionType} from '../actions/customer-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

export interface State {
  search: CustomerSearchQuery;
  searchByType: CustomerSearchByType;
  loading: boolean;
  matching: Customer[];
}

const initialState: State = {
  search: undefined,
  searchByType: undefined,
  loading: false,
  matching: []
};

function reducer(state: State = initialState, action: CustomerSearchActions) {
  switch (action.type) {
    case CustomerSearchActionType.Search: {
      return {
        ...state,
        search: action.payload,
        loading: true
      };
    }

    case CustomerSearchActionType.SearchByType: {
      return {
        ...state,
        searchByType: action.payload,
        loading: true
      };
    }

    case CustomerSearchActionType.SearchSuccess: {
      return {
        ...state,
        matching: action.payload,
        loading: false
      };
    }

    case CustomerSearchActionType.SearchFailed: {
      return {
        ...state,
        matching: [],
        loading: false
      };
    }

    default: {
      return {...state};
    }
  }
}

export function createReducerFor(targetType: ActionTargetType) {
  return function(state: State = initialState, action: CustomerSearchActions) {
    if (targetType === action.targetType) {
      return reducer(state, action);
    } else {
      return state;
    }
  };
}

export const getMatching = (state: State) => state.matching;

export const getLoading = (state: State) => state.loading;
