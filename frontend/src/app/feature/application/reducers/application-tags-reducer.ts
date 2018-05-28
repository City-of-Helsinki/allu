import {ApplicationTagActions, ApplicationTagActionType} from '../actions/application-tag-actions';
import {ApplicationTag} from '../../../model/application/tag/application-tag';

export interface State {
  current: ApplicationTag[];
}

const initialState: State = {
  current: []
};

export function reducer(state: State = initialState, action: ApplicationTagActions) {
  switch (action.type) {
    case ApplicationTagActionType.LoadSuccess:
    case ApplicationTagActionType.SaveSuccess: {
      return {
        ...state,
        current: action.payload,
      };
    }

    case ApplicationTagActionType.AddSuccess: {
      return {
        ...state,
        current: state.current.concat(action.payload)
      };
    }

    case ApplicationTagActionType.RemoveSuccess: {
      return {
        ...state,
        current: state.current.filter(tag => tag.type !== action.payload.type)
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getCurrent = (state: State) => state.current;
