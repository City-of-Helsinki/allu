import {ApplicationActions, ApplicationActionTypes} from '../actions/application-actions';
import {Application} from '../../../model/application/application';
import {Page} from '../../../model/common/page';

export interface State {
  loading: boolean;
  applications: Application[];
  page: Page<Application>;
}

const initialState: State = {
  loading: false,
  applications: [],
  page: new Page<Application>()
};

export function reducer(state: State = initialState, action: ApplicationActions) {
  switch (action.type) {
    case ApplicationActionTypes.Load: {
      return {
        ...state,
        loading: true
      };
    }

    case ApplicationActionTypes.LoadSuccess: {
      return {
        ...state,
        loading: false,
        applications: action.payload
      };
    }

    case ApplicationActionTypes.LoadFailed: {
      return {
        ...state,
        loading: false,
        applications: []
      };
    }

    case ApplicationActionTypes.AddSuccess: {
      return {
        ...state,
        applications: state.applications.concat(action.payload)
      };
    }

    case ApplicationActionTypes.RemoveSuccess: {
      return {
        ...state,
        applications: state.applications.filter(app => app.id !== action.payload)
      };
    }

    default:
      return {...state};
  }
}

export const getLoading = (state: State) => state.loading;

export const getApplications = (state: State) => state.applications;

export const getPage = (state: State) => state.page;
