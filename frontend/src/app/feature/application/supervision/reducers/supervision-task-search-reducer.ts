import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Page} from '@model/common/page';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';
import {createSelector, MemoizedSelector} from '@ngrx/store';
import {ArrayUtil} from '@util/array-util';
import {SupervisionTaskSearchCriteria} from '@model/application/supervision/supervision-task-search-criteria';
import {
  SupervisionTaskSearchActions,
  SupervisionTaskSearchActionType
} from '@feature/application/supervision/actions/supervision-task-search-actions';
import {SupervisionWorkItem} from '@model/application/supervision/supervision-work-item';

export interface State {
  parameters: SupervisionTaskSearchCriteria;
  sort: Sort;
  pageRequest: PageRequest;
  searching: boolean;
  matching: Page<SupervisionWorkItem>;
  selected: number[];
}

const initialState: State = {
  parameters: undefined,
  sort: new Sort(),
  pageRequest: new PageRequest(0, 25),
  searching: false,
  matching: new Page<SupervisionWorkItem>(),
  selected: []
};

export function reducer(state: State = initialState, action: SupervisionTaskSearchActions) {
  switch (action.type) {

    case SupervisionTaskSearchActionType.SetSearchQuery: {
      return {
        ...state,
        parameters: action.payload,
        pageRequest: {...state.pageRequest, page: 0}
      };
    }

    case SupervisionTaskSearchActionType.SetSort: {
      return {
        ...state,
        sort: action.payload,
        pageRequest: {...state.pageRequest, page: 0}
      };
    }

    case SupervisionTaskSearchActionType.SetPaging: {
      return {
        ...state,
        pageRequest: action.payload
      };
    }

    case SupervisionTaskSearchActionType.ResetToFirstPage: {
      return {
        ...state,
        pageRequest: {...state.pageRequest, page: 0}
      };
    }

    case SupervisionTaskSearchActionType.Search: {
      return {
        ...state,
        searching: true
      };
    }

    case SupervisionTaskSearchActionType.SearchSuccess: {
      return {
        ...state,
        matching: action.payload,
        searching: false
      };
    }

    case SupervisionTaskSearchActionType.SearchFailed: {
      return {
        ...state,
        matching: new Page<SupervisionWorkItem>(),
        searching: false
      };
    }

    case SupervisionTaskSearchActionType.ToggleSelect: {
      return {
        ...state,
        selected: ArrayUtil.removeExistingAddMissing(state.selected, action.payload)
      };
    }

    case SupervisionTaskSearchActionType.ToggleSelectAll: {
      const selected = getAllSelected(state) ? [] : getMatchingIds(state);
      return {
        ...state,
        selected
      };
    }

    case SupervisionTaskSearchActionType.ClearSelected: {
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
  return function(state: State = initialState, action: SupervisionTaskSearchActions) {
    if (targetType === action.targetType) {
      return reducer(state, action);
    } else {
      return state;
    }
  };
}

export const getMatchingSupervisionTasksList = (state: State) => state.matching
  ? state.matching.content
  : [];

export const getMatchingSupervisionTasks = (state: State) => state.matching;

export const getMatchingIds = (state: State) => getMatchingSupervisionTasksList(state).map(task => task.id);

export const getSearching = (state: State) => state.searching;

export const getParameters = (state: State) => state.parameters;

export const getSort = (state: State) => state.sort;

export const getPageRequest = (state: State) => state.pageRequest;

export const getSelected = (state: State) => state.selected;

export const getAllSelected = (state: State) => ArrayUtil.containSame(state.selected, getMatchingIds(state));

export const getSomeSelected = (state: State) => state.selected ? state.selected.length > 0 : false;

export function createSupervisionTaskSearchSelectors(getState: MemoizedSelector<object, State>) {
  return {
    getMatching: createSelector(getState, getMatchingSupervisionTasks),
    getMatchingList: createSelector(getState, getMatchingSupervisionTasksList),
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
