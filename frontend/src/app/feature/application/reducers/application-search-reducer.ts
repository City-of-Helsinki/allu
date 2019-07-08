import {Application} from '@model/application/application';
import {ApplicationSearchActions, ApplicationSearchActionType} from '../actions/application-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {ApplicationSearchQuery, fromApplicationIdAndName} from '@model/search/ApplicationSearchQuery';
import {Page} from '@model/common/page';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';
import {createSelector, MemoizedSelector} from '@ngrx/store';
import {ArrayUtil} from '@util/array-util';

export interface State {
  parameters: ApplicationSearchQuery;
  sort: Sort;
  pageRequest: PageRequest;
  searching: boolean;
  matchingApplications: Page<Application>;
  selected: number[];
}

const initialState: State = {
  parameters: undefined,
  sort: new Sort(),
  pageRequest: new PageRequest(0, 25),
  searching: false,
  matchingApplications: new Page<Application>(),
  selected: []
};

export function reducer(state: State = initialState, action: ApplicationSearchActions) {
  switch (action.type) {

    case ApplicationSearchActionType.SetSearchQuery: {
      return {
        ...state,
        parameters: action.payload,
        pageRequest: {...state.pageRequest, page: 0}
      };
    }

    case ApplicationSearchActionType.SetSort: {
      return {
        ...state,
        sort: action.payload,
        pageRequest: {...state.pageRequest, page: 0}
      };
    }

    case ApplicationSearchActionType.SetPaging: {
      return {
        ...state,
        pageRequest: action.payload
      };
    }

    case ApplicationSearchActionType.ResetToFirstPage: {
      return {
        ...state,
        pageRequest: {...state.pageRequest, page: 0}
      };
    }

    case ApplicationSearchActionType.SearchByNameOrId: {
      return {
        ...state,
        parameters: fromApplicationIdAndName(action.payload, action.payload),
        searching: true,
        matchingApplications: new Page<Application>()
      };
    }

    case ApplicationSearchActionType.Search: {
      return {
        ...state,
        searching: true
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
        matchingApplications: new Page<Application>(),
        searching: false
      };
    }

    case ApplicationSearchActionType.ToggleSelect: {
      return {
        ...state,
        selected: ArrayUtil.removeExistingAddMissing(state.selected, action.payload)
      };
    }

    case ApplicationSearchActionType.ToggleSelectAll: {
      const selected = getAllSelected(state) ? [] : getMatchingIds(state);
      return {
        ...state,
        selected
      };
    }

    case ApplicationSearchActionType.ClearSelected: {
      return {
        ...state,
        selected: []
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

export const getMatchingApplicationsList = (state: State) => state.matchingApplications
  ? state.matchingApplications.content
  : [];

export const getMatchingApplications = (state: State) => state.matchingApplications;

export const getMatchingIds = (state: State) => getMatchingApplicationsList(state).map(app => app.id);

export const getSearching = (state: State) => state.searching;

export const getParameters = (state: State) => state.parameters;

export const getSort = (state: State) => state.sort;

export const getPageRequest = (state: State) => state.pageRequest;

export const getSelected = (state: State) => state.selected;

export const getAllSelected = (state: State) => ArrayUtil.containSame(state.selected, getMatchingIds(state));

export const getSomeSelected = (state: State) => state.selected ? state.selected.length > 0 : false;

export function createApplicationSearchSelectors(getState: MemoizedSelector<object, State>) {
  return {
    getMatching: createSelector(getState, getMatchingApplications),
    getMatchingList: createSelector(getState, getMatchingApplicationsList),
    getMatchingIds: createSelector(getState, getMatchingIds),
    getSearching: createSelector(getState, getSearching),
    getParameters: createSelector(getState, getParameters),
    getSort: createSelector(getState, getSort),
    getPageRequest: createSelector(getState, getPageRequest),
    getSelected: createSelector(getState, getSelected),
    getAllSelected: createSelector(getState, getAllSelected),
    getSomeSelected: createSelector(getState, getSomeSelected)
  };
}
