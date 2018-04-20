import {
  createSelector,
  createFeatureSelector,
  ActionReducerMap,
} from '@ngrx/store';
import * as fromApplications from './application-reducer';
import * as fromProject from './project-reducer';
import * as fromSearch from './application-search-reducer';
import * as fromParentProjects from './parent-project-reducer';
import * as fromChildProjects from './child-project-reducer';
import * as fromRoot from '../../allu/reducers/index';

export interface ProjectState {
  project: fromProject.State;
  applications: fromApplications.State;
  search: fromSearch.State;
  parents: fromParentProjects.State;
  children: fromChildProjects.State;
}

export interface State extends fromRoot.State {
  project: ProjectState;
}

export const reducers: ActionReducerMap<ProjectState> = {
  project: fromProject.reducer,
  applications: fromApplications.reducer,
  search: fromSearch.reducer,
  parents: fromParentProjects.reducer,
  children: fromChildProjects.reducer
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

export const getProjectDistricts = createSelector(
  getCurrentProject,
  fromRoot.getAllCityDistricts,
  (project, districts) => project.cityDistricts.map(id => districts.get(id))
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

// Parent selectors
export const getParentsState = createSelector(
  getProjectState,
  (state: ProjectState) => state.parents
);

export const getParentProjects = createSelector(
  getParentsState,
  fromParentProjects.getProjects
);

// Child selectors
export const getChildrenState = createSelector(
  getProjectState,
  (state: ProjectState) => state.children
);

export const getChildProjects = createSelector(
  getChildrenState,
  fromChildProjects.getProjects
);

export const getRelatedProjects = createSelector(
  getParentProjects,
  getChildProjects,
  (parents, children) => [].concat(parents, children)
);
