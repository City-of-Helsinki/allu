import {Project} from '../../../model/project/project';
import {ProjectActions, ProjectActionTypes} from '../actions/project-actions';

export interface State {
  loading: boolean;
  current: Project;
}

const initialState: State = {
  loading: false,
  current: undefined
};

export function reducer(state: State = initialState, action: ProjectActions) {
  switch (action.type) {
    case ProjectActionTypes.Load: {
      return {
        ...state,
        loading: true
      };
    }

    case ProjectActionTypes.LoadSuccess: {
      return {
        ...state,
        loading: false,
        current: action.payload
      };
    }

    case ProjectActionTypes.LoadFailed: {
      return {
        ...state,
        loading: false
      };
    }

    default:
      return {...state};
  }
}

export const getCurrent = (state: State) => state.current;

