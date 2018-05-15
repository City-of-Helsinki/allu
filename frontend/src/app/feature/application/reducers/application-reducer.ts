import {Application} from '../../../model/application/application';
import {ApplicationActions, ApplicationActionType} from '../actions/application-actions';

export interface State {
  loaded: boolean;
  loading: boolean;
  current: Application;
}

const initialState: State = {
  loaded: false,
  loading: false,
  current: undefined
};

export function reducer(state: State = initialState, action: ApplicationActions) {
  switch (action.type) {
    case ApplicationActionType.Load: {
      return {
        ...state,
        loading: true,
        loaded: false
      };
    }

    case ApplicationActionType.LoadSuccess: {
      return {
        ...state,
        loading: false,
        loaded: true,
        current: action.payload
      };
    }

    case ApplicationActionType.LoadFailed: {
      return {
        ...state,
        loading: false,
        loaded: true
      };
    }

    default:
      return {...state};
  }
}

export const getCurrent = (state: State) => state.current;

export const getLoaded = (state: State) => state.loaded;

