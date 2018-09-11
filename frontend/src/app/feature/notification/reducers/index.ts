import * as fromRoot from '@feature/allu/reducers/index';
import * as fromNotification from './notification-reducer';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';

export interface NotificationState {
  notification: fromNotification.State;
}

export interface State extends fromRoot.State {
  notification: NotificationState;
}

export const reducers: ActionReducerMap<NotificationState> = {
  notification: fromNotification.reducer
};

export const getNotificationState = createFeatureSelector('notification');

export const getNotificationEntityState = createSelector(
  getNotificationState,
  (state: NotificationState) => state.notification
);

export const getSuccess = createSelector(
  getNotificationEntityState,
  fromNotification.getSuccess
);

export const getError = createSelector(
  getNotificationEntityState,
  fromNotification.getError
);
