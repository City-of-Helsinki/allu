import {Project} from '../../../model/project/project';
import {ChildProjectActions, ChildProjectActionType} from '../actions/child-project-actions';

export interface State {
  loading: boolean;
  projects: Project[];
}

export const initialState: State = {
  loading: false,
  projects: []
};

export function reducer(state: State = initialState, action: ChildProjectActions) {
  switch (action.type) {
    case ChildProjectActionType.Load: {
      return {
        ...state,
        loading: true
      };
    }

    case ChildProjectActionType.LoadSuccess: {
      return {
        ...state,
        loading: false,
        projects: action.payload
      };
    }

    case ChildProjectActionType.LoadFailed: {
      return {
        ...state,
        loading: false
      };
    }

    case ChildProjectActionType.AddSuccess: {
      return {
        ...state,
        projects: state.projects.concat(action.payload)
      };
    }

    default:
      return {...state};
  }
}

export const getProjects = (state: State) => state.projects;
export const getLoading = (state: State) => state.loading;
