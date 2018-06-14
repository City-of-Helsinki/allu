import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';

import * as fromApplication from './application-reducer';
import * as fromComments from '../../comment/reducers/comment-reducer';
import * as fromApplicationComments from '../../application/reducers/application-comments-reducer';
import * as fromTags from './application-tags-reducer';
import * as fromRoot from '../../allu/reducers/index';
import {Application} from '../../../model/application/application';
import {ApplicationTagType} from '../../../model/application/tag/application-tag-type';
import {ApplicationTag} from '../../../model/application/tag/application-tag';
import * as fromHistory from '../../history/reducers/history-reducer';
import * as fromApplicationHistory from '../reducers/application-history-reducer';
import {ClientApplicationData} from '../../../model/application/client-application-data';

export interface ApplicationState {
  application: fromApplication.State;
  comments: fromComments.State;
  tags: fromTags.State;
  history: fromHistory.State;
}

export interface State extends fromRoot.State {
  application: ApplicationState;
}

export const reducers: ActionReducerMap<ApplicationState> = {
  application: fromApplication.reducer,
  comments: fromApplicationComments.reducer,
  tags: fromTags.reducer,
  history: fromApplicationHistory.reducer
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

export const getApplicationLoaded = createSelector(
  getApplicationEntitiesState,
  fromApplication.getLoaded
);

export const getType = createSelector(
  getApplicationEntitiesState,
  fromApplication.getType
);

export const getKindsWithSpecifiers = createSelector(
  getApplicationEntitiesState,
  fromApplication.getKindsWithSpecifiers
);

export const getClientData = createSelector(
  getApplicationEntitiesState,
  fromApplication.getClientData
);

export const getPendingKind = createSelector(
  getCurrentApplication,
  getClientData,
  (application: Application, clientData: ClientApplicationData) => {
    const noKind = application.kinds.length === 0;
    const pendingKind = !!clientData ? clientData.clientApplicationKind : undefined;
    return (noKind && pendingKind) ? pendingKind : undefined;
  }
);

export const getMeta = createSelector(
  getApplicationEntitiesState,
  fromApplication.getMeta
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

export const getDirection = createSelector(
  getCommentsEntitiesState,
  fromComments.getDirection
);

export const getSortedComments = createSelector(
  getAllComments,
  getDirection,
  (comments, direction) => comments.slice().sort(fromComments.sort(direction))
);

/**
 * Selectors for tags
 */
export const getTagsState = createSelector(
  getApplicationState,
  (state: ApplicationState) => state.tags
);

export const getTags = createSelector(
  getTagsState,
  fromTags.getCurrent
);

export const hasTag = (tagType: ApplicationTagType) => createSelector(
  getTags,
  (tags: ApplicationTag[]) => tags.some(tag => tagType === ApplicationTagType[tag.type])
);

// History selectors
export const getHistoryState = createSelector(
  getApplicationState,
  (state: ApplicationState) => state.history
);

export const getHistory = createSelector(
  getHistoryState,
  fromHistory.getHistory
);

export const getFieldsVisible = createSelector(
  getHistoryState,
  fromHistory.getFieldsVisible
);
