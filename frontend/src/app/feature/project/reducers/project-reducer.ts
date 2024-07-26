import {Project} from '../../../model/project/project';
import {ProjectActions, ProjectActionTypes} from '../actions/project-actions';
import {StructureMeta} from '../../../model/application/meta/structure-meta';
import {ProjectMetaActions, ProjectMetaActionType} from '../actions/project-meta-actions';

export interface State {
  loaded: boolean;
  loading: boolean;
  current: Project;
  meta: StructureMeta;
  showBasicInfo: boolean;
}

const initialState: State = {
  loaded: false,
  loading: false,
  current: undefined,
  meta: undefined,
  showBasicInfo: true
};

export function reducer(state: State = initialState, action: ProjectActions | ProjectMetaActions) {
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

    case ProjectActionTypes.DeleteSuccess: {
      return {
        ...state,
        current: undefined,
        showBasicInfo: true
      };
    }

    case ProjectActionTypes.ShowBasicInfo: {
      return {
        ...state,
        showBasicInfo: action.payload
      };
    }

    case ProjectMetaActionType.LoadSuccess: {
      return {
        ...state,
        meta: action.payload
      };
    }

    default:
      return {...state};
  }
}

export const getCurrent = (state: State) => state.current;

export const getLoaded = (state: State) => state.loaded;

export const getShowBasicInfo = (state: State) => state.showBasicInfo;

export const getMeta = (state: State) => state.meta;

