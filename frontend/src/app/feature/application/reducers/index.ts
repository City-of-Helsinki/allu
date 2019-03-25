import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';

import * as fromApplication from './application-reducer';
import * as fromComments from '@feature/comment/reducers/comment-reducer';
import * as fromApplicationComments from '@feature/application/reducers/application-comments-reducer';
import * as fromTags from './application-tags-reducer';
import * as fromRoot from '@feature/allu/reducers/index';
import * as fromReplacementHistory from '@feature/application/reducers/application-replacement-history-reducer';
import * as fromApplicationSearch from '@feature/application/reducers/application-search-reducer';
import {Application} from '@model/application/application';
import {ApplicationTagType} from '@model/application/tag/application-tag-type';
import {ApplicationTag} from '@model/application/tag/application-tag';
import * as fromHistory from '@feature/history/reducers/history-reducer';
import * as fromApplicationHistory from '../reducers/application-history-reducer';
import {ClientApplicationData} from '@model/application/client-application-data';
import {NumberUtil} from '@util/number.util';
import {ArrayUtil} from '@util/array-util';
import {InjectionToken} from '@angular/core';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {ApplicationKind} from '@model/application/type/application-kind';

export interface ApplicationState {
  application: fromApplication.State;
  comments: fromComments.State;
  tags: fromTags.State;
  history: fromHistory.State;
  replacementHistory: fromReplacementHistory.State;
  cableReportSearch: fromApplicationSearch.State;
  placementContractSearch: fromApplicationSearch.State;
}

export interface State extends fromRoot.State {
  application: ApplicationState;
}

export const reducers: ActionReducerMap<ApplicationState> = {
  application: fromApplication.reducer,
  comments: fromApplicationComments.reducer,
  tags: fromTags.reducer,
  history: fromApplicationHistory.reducer,
  replacementHistory: fromReplacementHistory.reducer,
  cableReportSearch: fromApplicationSearch.createReducerFor(ActionTargetType.CableReport),
  placementContractSearch: fromApplicationSearch.createReducerFor(ActionTargetType.PlacementContract)
};

export const reducersToken = new InjectionToken<ActionReducerMap<State>>('Application reducers');

export const reducersProvider = [
  { provide: reducersToken, useValue: reducers }
];

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

export const getKind = createSelector(
  getKindsWithSpecifiers,
  (kws) => (<ApplicationKind>ArrayUtil.first(Object.keys(kws)))
);

export const isFromExternalSystem = createSelector(
  getCurrentApplication,
  (app: Application) => app && NumberUtil.isDefined(app.externalOwnerId)
);

export const getClientData = createSelector(
  getApplicationEntitiesState,
  fromApplication.getClientData
);

export const getPendingKind = createSelector(
  getClientData,
  (clientData: ClientApplicationData) => !!clientData ? clientData.clientApplicationKind : undefined
);

export const getPendingCustomerWithContacts = createSelector(
  getClientData,
  (clientData: ClientApplicationData) => !!clientData ? clientData.customer : undefined
);

export const getPendingInvoicingCustomer = createSelector(
  getClientData,
  (clientData: ClientApplicationData) => !!clientData ? clientData.invoicingCustomer : undefined
);

export const hasPendingCustomerInfo = createSelector(
  getPendingCustomerWithContacts,
  pending => !!pending
);

export const hasPendingClientData = createSelector(
  getClientData,
  (clientData: ClientApplicationData) => !!clientData
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
  (tags: ApplicationTag[]) => tags.some(tag => tagType === tag.type)
);

export const hasTags = (tagTypes: ApplicationTagType[]) => createSelector(
  getTags,
  (tags: ApplicationTag[]) => {
    const types = tags.map(tag => tag.type);
    return ArrayUtil.anyMatch(tagTypes, types);
  }
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

// Replacement history selectors
export const getReplacementHistoryState = createSelector(
  getApplicationState,
  (state: ApplicationState) => state.replacementHistory
);

export const getReplacementHistory = createSelector(
  getReplacementHistoryState,
  fromReplacementHistory.getReplacementHistory
);

// Cable report search selectors
export const getCableReportSearchState = createSelector(
  getApplicationState,
  (state: ApplicationState) => state.cableReportSearch
);

export const getMatchingCableReports = createSelector(
  getCableReportSearchState,
  fromApplicationSearch.getMatchingApplications
);

// Placement contract search selectors
export const getPlacementContractSearchState = createSelector(
  getApplicationState,
  (state: ApplicationState) => state.placementContractSearch
);

export const getMatchingPlacementContracts = createSelector(
  getPlacementContractSearchState,
  fromApplicationSearch.getMatchingApplications
);

export const getMatchingByTargetType = (type: ActionTargetType) => {
  switch (type) {
    case ActionTargetType.CableReport:
      return getMatchingCableReports;
    case ActionTargetType.PlacementContract:
      return getMatchingPlacementContracts;
    default:
      throw new Error(`Invalid target type for matching applications ${type}`);
  }
};
