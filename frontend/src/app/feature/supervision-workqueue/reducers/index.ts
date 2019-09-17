import * as fromSupervisionTaskSearch from '@feature/application/supervision/reducers/supervision-task-search-reducer';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {InjectionToken} from '@angular/core';
import * as fromWorkQueue from '@feature/workqueue/reducers/workqueue-reducer';

export interface State {
  workQueue: fromWorkQueue.State;
  search: fromSupervisionTaskSearch.State;
}

export const reducers: ActionReducerMap<State> = {
  workQueue: fromWorkQueue.createReducerFor(ActionTargetType.SupervisionTaskWorkQueue),
  search: fromSupervisionTaskSearch.createReducerFor(ActionTargetType.SupervisionTaskWorkQueue)
};

export const reducersToken = new InjectionToken<ActionReducerMap<State>>('Supervision work queue reducers');

export const reducersProvider = [
  { provide: reducersToken, useValue: reducers }
];

export const getSupervisionWorkQueueState = createFeatureSelector<State>('supervisionWorkQueue');

// WorkQueue selectors
export const getWorkQueueState = createSelector(
  getSupervisionWorkQueueState,
  (state: State) => state.workQueue
);

export const getTab = createSelector(
  getWorkQueueState,
  fromWorkQueue.getTab
);

// SearchForCurrentCustomer selectors
export const getSearchState = createSelector(
  getSupervisionWorkQueueState,
  (state: State) => state.search
);

export const {
  getMatching: getMatching,
  getMatchingIds: getMatchingIds,
  getSearching: getSearching,
  getParameters: getParameters,
  getSort: getSort,
  getPageRequest: getPageRequest,
  getSelected: getSelected,
  getAllSelected: getAllSelected,
  getSomeSelected: getSomeSelected
} = fromSupervisionTaskSearch.createSupervisionTaskSearchSelectors(getSearchState);

