import * as fromWorkQueue from '@feature/workqueue/reducers/workqueue-reducer';
import * as fromApplicationSearch from '@feature/application/reducers/application-search-reducer';
import {createApplicationSearchSelectors} from '@feature/application/reducers/application-search-reducer';
import * as fromRoot from '@feature/allu/reducers';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {InjectionToken} from '@angular/core';

export interface ApplicationWorkQueueState {
  workQueue: fromWorkQueue.State;
  search: fromApplicationSearch.State;
}

export interface State extends fromRoot.State {
  workQueue: ApplicationWorkQueueState;
}

export const reducers: ActionReducerMap<ApplicationWorkQueueState> = {
  workQueue: fromWorkQueue.createReducerFor(ActionTargetType.ApplicationWorkQueue),
  search: fromApplicationSearch.createReducerFor(ActionTargetType.ApplicationWorkQueue)
};

export const reducersToken = new InjectionToken<ActionReducerMap<State>>('Application work queue reducers');

export const reducersProvider = [
  { provide: reducersToken, useValue: reducers }
];

export const getApplicationWorkQueueState = createFeatureSelector<ApplicationWorkQueueState>('workQueue');

// WorkQueue selectors
export const getWorkQueueState = createSelector(
  getApplicationWorkQueueState,
  (state: ApplicationWorkQueueState) => state.workQueue
);

export const getTab = createSelector(
  getWorkQueueState,
  fromWorkQueue.getTab
);

// SearchForCurrentCustomer selectors
export const getSearchState = createSelector(
  getApplicationWorkQueueState,
  (state: ApplicationWorkQueueState) => state.search
);

export const {
  getMatching: getMatchingApplications,
  getMatchingList: getMatchingApplicationsList,
  getMatchingIds: getMatchingApplicationIds,
  getSearching: getSearchingApplications,
  getParameters: getApplicationSearchParameters,
  getSort: getApplicationSearchSort,
  getPageRequest: getApplicationSearchPageRequest,
  getSelected: getSelectedApplications,
  getAllSelected: getAllApplicationsSelected,
  getSomeSelected: getSomeApplicationsSelected
} = createApplicationSearchSelectors(getSearchState);

export const getSelectedApplicationEntities = createSelector(
  getMatchingApplicationsList,
  getSelectedApplications,
  (matching, selected) => matching.filter(app => selected.indexOf(app.id) >= 0)
);
