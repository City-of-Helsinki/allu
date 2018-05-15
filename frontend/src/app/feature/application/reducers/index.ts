import {
  createSelector,
  createFeatureSelector,
  ActionReducerMap
} from '@ngrx/store';

import * as fromApplication from './application-reducer';
import * as fromComments from '../../comment/reducers/comment-reducer';
import * as fromApplicationComments from '../../comment/reducers/comment-reducer';
import * as fromRoot from '../../allu/reducers/index';
import {Application} from '../../../model/application/application';

export interface ApplicationState {
  application: fromApplication.State;
  comments: fromComments.State;
}

export interface State extends fromRoot.State {
  application: ApplicationState;
}

export const reducers: ActionReducerMap<ApplicationState> = {
  application: fromApplication.reducer,
  comments: fromApplicationComments.reducer
};

export const getApplicationState = createFeatureSelector<ApplicationState>('application');

// Application selectors
export const getApplicationEntitiesState = createSelector(
  getApplicationState,
  (state: ApplicationState) => state.application
);

export const getCurrentApplication = createSelector(
  getApplicationEntitiesState,
  fromApplication.getCurrent
);

export const getIsNew = createSelector(
  getCurrentApplication,
  (app: Application) => app ? app.id === undefined : true
);

export const getApplicationLoaded = createSelector(
  getApplicationEntitiesState,
  fromApplication.getLoaded
);

export const getCommentsEntitiesState = createSelector(
  getApplicationState,
  (state: ApplicationState) => state.comments
);

export const {
  selectIds: getCommentIds,
  selectEntities: getCommentEntities,
  selectAll: getAllComments,
  selectTotal: getCommentCount
} = fromComments.adapter.getSelectors(getCommentsEntitiesState);
