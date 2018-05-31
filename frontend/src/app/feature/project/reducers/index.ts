import {
  createSelector,
  createFeatureSelector,
  ActionReducerMap,
} from '@ngrx/store';
import * as fromApplications from './application-reducer';
import * as fromProject from './project-reducer';
import * as fromSearch from './application-search-reducer';
import * as fromParentProjects from './parent-project-reducer';
import * as fromCustomerSearch from './customer-search-reducer';
import * as fromChildProjects from './child-project-reducer';
import * as fromApplicationBasket from './application-basket-reducer';
import * as fromRoot from '../../allu/reducers/index';
import * as fromComments from '../../comment/reducers/comment-reducer';
import * as fromProjectComments from './project-comments-reducer';
import * as fromHistory from '../../history/reducers/history-reducer';
import * as fromProjectHistory from './project-history-reducer';
import {Project} from '../../../model/project/project';
import {SortDirection} from '../../../model/common/sort';

export interface ProjectState {
  project: fromProject.State;
  applications: fromApplications.State;
  applicationSearch: fromSearch.State;
  parents: fromParentProjects.State;
  children: fromChildProjects.State;
  customerSearch: fromCustomerSearch.State;
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
  applicationSearch: fromSearch.reducer,
  parents: fromParentProjects.reducer,
  children: fromChildProjects.reducer,
  customerSearch: fromCustomerSearch.reducer,
  applicationBasket: fromApplicationBasket.reducer,
  comments: fromProjectComments.reducer,
  history: fromProjectHistory.reducer
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

export const getIsNewProject = createSelector(
  getCurrentProject,
  (project: Project) => project ? project.id === undefined : true
);

export const getProjectLoaded = createSelector(
  getProjectEntitiesState,
  fromProject.getLoaded
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

// Search selectors
export const getApplicationSearchState = createSelector(
  getProjectState,
  (state: ProjectState) => state.applicationSearch
);

export const getMatchingApplications = createSelector(
  getApplicationSearchState,
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

// Customer selectors
export const getCustomerSearchState = createSelector(
  getProjectState,
  (state: ProjectState) => state.customerSearch
);

export const getMatchingCustomers = createSelector(
  getCustomerSearchState,
  fromCustomerSearch.getMatchingCustomers
);

export const getMatchingContacts = createSelector(
  getCustomerSearchState,
  fromCustomerSearch.getMatchingContacts
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

// History selectors
export const getHistoryState = createSelector(
  getProjectState,
  (state: ProjectState) => state.history
);

export const getHistory = createSelector(
  getHistoryState,
  fromHistory.getHistory
);

