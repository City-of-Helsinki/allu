import {Project} from '../../../model/project/project';
import {ProjectActions, ProjectActionTypes} from '../actions/project-actions';

export interface State {
  loaded: boolean;
  loading: boolean;
  current: Project;
}

const initialState: State = {
  loaded: false,
  loading: false,
  current: undefined
};

export function reducer(state: State = initialState, action: ProjectActions) {
  switch (action.type) {
    case ProjectActionTypes.Load: {
      return {
        ...state,
        loading: true,
        loaded: false
      };
    }

    case ProjectActionTypes.LoadSuccess: {
      return {
        ...state,
        loading: false,
        loaded: true,
        current: action.payload
      };
    }

    case ProjectActionTypes.SaveSuccess: {
      return {
        ...state,
        loading: false,
        loaded: true,
        current: action.payload
      };
    }

    case ProjectActionTypes.LoadFailed: {
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

