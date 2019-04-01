import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import * as fromApplications from './application-reducer';
import * as fromProject from './project-reducer';
import * as fromApplicationSearch from '@feature/application/reducers/application-search-reducer';
import * as fromProjectSearch from './project-search-reducer';
import * as fromParentProjects from './parent-project-reducer';
import * as fromChildProjects from './child-project-reducer';
import * as fromApplicationBasket from './application-basket-reducer';
import * as fromRoot from '@feature/allu/reducers/index';
import * as fromComments from '@feature/comment/reducers/comment-reducer';
import * as fromProjectComments from './project-comments-reducer';
import * as fromHistory from '@feature/history/reducers/history-reducer';
import * as fromProjectHistory from './project-history-reducer';
import {Project} from '@model/project/project';
import {SortDirection} from '@model/common/sort';
import {ArrayUtil} from '@util/array-util';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {InjectionToken} from '@angular/core';

export interface ProjectState {
  project: fromProject.State;
  applications: fromApplications.State;
  applicationSearch: fromApplicationSearch.State;
  projectSearch: fromProjectSearch.State;
  parents: fromParentProjects.State;
  children: fromChildProjects.State;
  applicationBasket: fromApplicationBasket.State;
  comments: fromComments.State;
  history: fromHistory.State;
}

export interface State extends fromRoot.State {
  project: ProjectState;
}

export const reducers: ActionReducerMap<ProjectState> = {
  project: fromProject.reducer,
  applications: fromApplications.reducer,
  applicationSearch: fromApplicationSearch.createReducerFor(ActionTargetType.Project),
  projectSearch: fromProjectSearch.reducer,
  parents: fromParentProjects.reducer,
  children: fromChildProjects.reducer,
  applicationBasket: fromApplicationBasket.reducer,
  comments: fromProjectComments.reducer,
  history: fromProjectHistory.reducer
};

export const reducersToken = new InjectionToken<ActionReducerMap<State>>('Project reducers');

export const reducersProvider = [
  { provide: reducersToken, useValue: reducers }
];

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

export const getIsNewProject = createSelector(
  getCurrentProject,
  (project: Project) => project ? project.id === undefined : true
);

export const getProjectLoaded = createSelector(
  getProjectEntitiesState,
  fromProject.getLoaded
);

export const getShowBasicInfo = createSelector(
  getProjectEntitiesState,
  fromProject.getShowBasicInfo
);

export const getMeta = createSelector(
  getProjectEntitiesState,
  fromProject.getMeta
);

export const getProjectDistricts = createSelector(
  getCurrentProject,
  fromRoot.getCityDistrictEntities,
  (project, districts) => project.cityDistricts.map(id => districts[id])
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

// Project search selectors
export const getProjectSearchState = createSelector(
  getProjectState,
  (state: ProjectState) => state.projectSearch
);

export const getMatchingProjects = createSelector(
  getProjectSearchState,
  fromProjectSearch.getMatching
);

// Application search selectors
export const getApplicationSearchState = createSelector(
  getProjectState,
  (state: ProjectState) => state.applicationSearch
);

export const getMatchingApplications = createSelector(
  getApplicationSearchState,
  fromApplicationSearch.getMatchingApplications
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

export const getParentProject = createSelector(
  getCurrentProject,
  getParentProjects,
  (current: Project, parents: Project[]) => ArrayUtil.first(parents, (p => p.id === current.parentId))
);

export const getParentProjectsLoading = createSelector(
  getParentsState,
  fromParentProjects.getLoading
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

export const getChildProjectsLoading = createSelector(
  getChildrenState,
  fromChildProjects.getLoading
);

export const getRelatedProjects = createSelector(
  getParentProjects,
  getChildProjects,
  (parents, children) => [].concat(parents, children)
);

// Application basket reducers
export const getApplicationBasketEntitiesState = createSelector(
  getProjectState,
  (state: ProjectState) => state.applicationBasket
);

export const {
  selectIds: getApplicationIdsInBasket,
  selectEntities: getApplicationEntitiesInBasket,
  selectAll: getAllApplicationsInBasket,
  selectTotal: getApplicationCountInBasket
} = fromApplicationBasket.adapter.getSelectors(getApplicationBasketEntitiesState);

export const getPendingApplicationIds = createSelector(
  getApplicationBasketEntitiesState,
  (state: fromApplicationBasket.State) => state.pending
);

export const getPendingApplications = createSelector(
  getPendingApplicationIds,
  getApplicationEntitiesInBasket,
  (ids, entities) => ids.map(id => entities[id])
);

// Comment selectors
export const getCommentsEntitiesState = createSelector(
  getProjectState,
  (state: ProjectState) => state.comments
);

export const {
  selectIds: getCommentIds,
  selectEntities: getCommentEntities,
  selectAll: getAllComments,
  selectTotal: getCommentCount
} = fromComments.adapter.getSelectors(getCommentsEntitiesState);

export const getDirection = createSelector(
  getCommentsEntitiesState,
  fromComments.getDirection
);

export const getSortedComments = createSelector(
  getAllComments,
  getDirection,
  (comments, direction) => comments.slice().sort(fromComments.sort(direction))
);

export const getLatestComments = (direction: SortDirection) => createSelector(
  getAllComments,
  comments => comments.slice().sort(fromComments.sort(direction))
);

export const getCommentsLoading = createSelector(
  getCommentsEntitiesState,
  fromComments.getLoading
);

// History selectors
export const getHistoryState = createSelector(
  getProjectState,
  (state: ProjectState) => state.history
);

export const getHistory = createSelector(
  getHistoryState,
  fromHistory.getHistory
);

export const getFieldsVisible = createSelector(
  getHistoryState,
  fromHistory.getFieldsVisible
);

