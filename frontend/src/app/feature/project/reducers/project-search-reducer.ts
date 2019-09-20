import {Project} from '@model/project/project';
import {ProjectSearchActionType, ProjectSearchParams, SearchActions} from '../actions/project-search-actions';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';
import {Page} from '@model/common/page';
import {createSelector, MemoizedSelector} from '@ngrx/store';
import {ProjectSearchQuery} from '@model/project/project-search-query';

export interface State {
  parameters: ProjectSearchQuery;
  sort: Sort;
  pageRequest: PageRequest;
  searching: boolean;
  matching: Page<Project>;
}

const initialState: State = {
  parameters: undefined,
  sort: new Sort(),
  pageRequest: new PageRequest(0, 25),
  searching: false,
  matching: new Page<Project>(),
};

export function reducer(state: State = initialState, action: SearchActions) {
  switch (action.type) {
    case ProjectSearchActionType.SetSearchQuery: {
      return {
        ...state,
        parameters: action.payload,
        pageRequest: {...state.pageRequest, page: 0}
      };
    }

    case ProjectSearchActionType.SetSort: {
      return {
        ...state,
        sort: action.payload,
        pageRequest: {...state.pageRequest, page: 0}
      };
    }

    case ProjectSearchActionType.SetPaging: {
      return {
        ...state,
        pageRequest: action.payload
      };
    }

    case ProjectSearchActionType.ResetToFirstPage: {
      return {
        ...state,
        pageRequest: {...state.pageRequest, page: 0}
      };
    }

    case ProjectSearchActionType.Search: {
      return {
        ...state,
        searching: true
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
        matching: new Page<Project>(),
        searching: false
      };
    }

    default:
      return {...state};
  }
}

export const getMatchingProjectList = (state: State) => state.matching
  ? state.matching.content
  : [];

export const getMatchingProjects = (state: State) => state.matching;

export const getMatchingIds = (state: State) => getMatchingProjectList(state).map(project => project.id);

export const getSearching = (state: State) => state.searching;

export const getParameters = (state: State) => state.parameters;

export const getSort = (state: State) => state.sort;

export const getPageRequest = (state: State) => state.pageRequest;

export function createProjectSearchSelectors(getState: MemoizedSelector<object, State>) {
  return {
    getMatching: createSelector(getState, getMatchingProjects),
    getMatchingList: createSelector(getState, getMatchingProjectList),
    getMatchingIds: createSelector(getState, getMatchingIds),
    getSearching: createSelector(getState, getSearching),
    getParameters: createSelector(getState, getParameters),
    getSort: createSelector(getState, getSort),
    getPageRequest: createSelector(getState, getPageRequest)
  };
}
