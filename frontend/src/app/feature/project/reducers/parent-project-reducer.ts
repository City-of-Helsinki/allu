import {Project} from '../../../model/project/project';
import {ParentProjectActions, ParentProjectActionType} from '../actions/parent-project-actions';

export interface State {
  loading: boolean;
  projects: Project[];
}

export const initialState: State = {
  loading: false,
  projects: []
};

export function reducer(state: State = initialState, action: ParentProjectActions) {
  switch (action.type) {
    case ParentProjectActionType.Load: {
      return {
        ...state,
        loading: true
      };
    }

    case ParentProjectActionType.LoadSuccess: {
      return {
        ...state,
        loading: false,
        projects: action.payload
      };
    }

    case ParentProjectActionType.LoadFailed: {
      return {
        ...state,
        loading: false
      };
    }

    default:
      return {...state};
  }
}

export const getProjects = (state: State) => state.projects;
export const getLoading = (state: State) => state.loading;
