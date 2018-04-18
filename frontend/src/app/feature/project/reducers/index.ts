import {
  createSelector,
  createFeatureSelector,
  ActionReducerMap,
} from '@ngrx/store';
import * as fromApplications from './application-reducer';
import * as fromProject from './project-reducer';
import * as fromSearch from './application-search-reducer';

export interface ProjectState {
  project: fromProject.State;
  applications: fromApplications.State;
  search: fromSearch.State;
}

export interface State {
  project: ProjectState;
}

export const reducers: ActionReducerMap<ProjectState> = {
  project: fromProject.reducer,
  applications: fromApplications.reducer,
  search: fromSearch.reducer
};

export const getProjectState = createFeatureSelector<ProjectState>('project');

// Project selectors
export const getProjectEntitiesState = createSelector(
  getProjectState,
  (state: ProjectState) => state.project
);

export const getCurrentProject = createSelector(
  getProjectEntitiesState,
  fromProject.getCurrent
);


// Application selectors
export const getProjectApplicationsState = createSelector(
  getProjectState,
  (state: ProjectState) => state.applications
);

export const getApplications = createSelector(
  getProjectApplicationsState,
  fromApplications.getApplications
);

export const getApplicationsLoading = createSelector(
  getProjectApplicationsState,
  fromApplications.getLoading
);

// Search selectors
export const getSearchState = createSelector(
  getProjectState,
  (state: ProjectState) => state.search
);

export const getMatchingApplications = createSelector(
  getSearchState,
  fromSearch.getMatchingApplications
);
