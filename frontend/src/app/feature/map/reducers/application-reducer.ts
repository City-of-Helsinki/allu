import {Application} from '@model/application/application';
import {ApplicationActions, ApplicationActionType} from '@feature/map/actions/application-actions';

export interface State {
  loading: boolean;
  applications: Application[];
}

export const initialState: State = {
  loading: false,
  applications: []
};

export function reducer(state: State = initialState, action: ApplicationActions) {
  switch (action.type) {
    case ApplicationActionType.Search: {
      return {
        ...state,
        loading: true
      };
    }

    case ApplicationActionType.SearchSuccess: {
      return {
        ...state,
        loading: false,
        applications: action.payload
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getLoading = (state: State) => state.loading;

export const getApplications = (state: State) => state.applications;
