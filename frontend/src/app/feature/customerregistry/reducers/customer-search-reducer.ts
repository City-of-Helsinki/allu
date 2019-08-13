import {CustomerSearchQuery} from '@service/customer/customer-search-query';
import {Customer} from '@model/customer/customer';
import {CustomerSearchActions, CustomerSearchActionType} from '@feature/customerregistry/actions/customer-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Page} from '@model/common/page';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';
import {createSelector, MemoizedSelector} from '@ngrx/store';

export interface State {
  search: CustomerSearchQuery;
  sort: Sort;
  pageRequest: PageRequest;
  loading: boolean;
  matching: Page<Customer>;
}

const initialState: State = {
  search: undefined,
  loading: false,
  sort: new Sort(),
  pageRequest: new PageRequest(0, 25),
  matching: new Page<Customer>()
};

function reducer(state: State = initialState, action: CustomerSearchActions) {
  switch (action.type) {
    case CustomerSearchActionType.Search:
    case CustomerSearchActionType.SearchByType: {
      return {
        ...state,
        search: action.payload.query,
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
        matching: new Page<Customer>(),
        loading: false
      };
    }

    case CustomerSearchActionType.SetSearchQuery: {
      return {
        ...state,
        search: action.payload,
        pageRequest: {...state.pageRequest, page: 0}
      };
    }

    case CustomerSearchActionType.SetSort: {
      return {
        ...state,
        sort: action.payload,
        pageRequest: {...state.pageRequest, page: 0}
      };
    }

    case CustomerSearchActionType.SetPaging: {
      return {
        ...state,
        pageRequest: action.payload
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

export const getMatchingList = (state: State) => getMatching(state).content;

export const getLoading = (state: State) => state.loading;

export const getSearch = (state: State) => state.search;

export const getSort = (state: State) => state.sort;

export const getPageRequest = (state: State) => state.pageRequest;

export function createCustomerSelectors(getState: MemoizedSelector<object, State>) {
  return {
    getMatching: createSelector(getState, getMatching),
    getMatchingList: createSelector(getState, getMatchingList),
    getLoading: createSelector(getState, getLoading),
    getSearch: createSelector(getState, getSearch),
    getSort: createSelector(getState, getSort),
    getPageRequest: createSelector(getState, getPageRequest),
  };
}
