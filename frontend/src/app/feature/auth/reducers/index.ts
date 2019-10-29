import {ActionReducerMap, createFeatureSelector, createSelector, select, Store} from '@ngrx/store';
import * as fromAuth from './auth-reducer';
import * as fromRoot from '@feature/allu/reducers';
import {User} from '@model/user/user';
import {allowedTagsByRoles, removableTagsByRoles} from '@model/application/tag/application-tag-type';
import {filter, switchMap} from 'rxjs/operators';

export interface AuthState {
  status: fromAuth.State;
}

export interface State extends fromRoot.State {
  auth: AuthState;
}

export const reducers: ActionReducerMap<AuthState> = {
  status: fromAuth.reducer
};

export const getAuthState = createFeatureSelector<AuthState>('auth');

export const getAuthStatusState = createSelector(
  getAuthState,
  (state: AuthState) => state.status
);

export const getUser = createSelector(
  getAuthStatusState,
  fromAuth.getUser
);

export const getLoggedIn = createSelector(
  getAuthStatusState,
  fromAuth.getLoggedIn
);

export const getLoggedInUser = (store: Store<fromRoot.State>) => store.pipe(
  select(getLoggedIn),
  filter(loggedIn => loggedIn),
  switchMap(() => store.pipe(select(getUser)))
);

export const getAllowedApplicationTypes = createSelector(
  getUser,
  (user: User) => !!user ? user.allowedApplicationTypes : []
);

export const getAllowedTags = createSelector(
  getUser,
  (user: User) => !!user ? allowedTagsByRoles(user.assignedRoles) : []
);

export const getRemovableTags = createSelector(
  getUser,
  (user: User) => !!user ? removableTagsByRoles(user.assignedRoles) : []
);
