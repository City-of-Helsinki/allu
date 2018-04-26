import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import * as fromAuth from './auth-reducer';
import * as fromRoot from '../../allu/reducers';

export interface AuthState {
  auth: fromAuth.State;
}

export interface State extends fromRoot.State {
  auth: AuthState;
}

export const reducers: ActionReducerMap<AuthState> = {
  auth: fromAuth.reducer
};

export const getAuthState = createFeatureSelector<fromAuth.State>('auth');

export const getUser = createSelector(
  getAuthState,
  fromAuth.getUser
);
