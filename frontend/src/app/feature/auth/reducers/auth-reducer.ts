import {User} from '../../../model/user/user';
import {AuthActions, AuthActionType} from '../actions/auth-actions';

export interface State {
  loggedIn: boolean;
  user: User;
}

export const initialState: State = {
  loggedIn: false,
  user: undefined
};

export function reducer(state: State = initialState, action: AuthActions) {
  switch (action.type) {
    case AuthActionType.LoggedIn: {
      return {
        ...state,
        loggedIn: true
      };
    }

    case AuthActionType.LoggedOut: {
      return {
        ...state,
        loggedIn: true,
        user: undefined
      };
    }

    case AuthActionType.LoggedUserLoaded: {
      return {
        ...state,
        user: action.payload
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getLoggedIn = (state: State) => state.loggedIn;

export const getUser = (state: State) => state.user;
