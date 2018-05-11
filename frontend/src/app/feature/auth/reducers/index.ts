import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import * as fromAuth from './auth-reducer';
import * as fromRoot from '../../allu/reducers';

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
