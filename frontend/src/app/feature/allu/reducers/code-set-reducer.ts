import {CodeSetTypeMap} from '../../../model/codeset/codeset';
import {CodeSetActions, CodeSetActionType} from '../actions/code-set-actions';

export interface State {
  codeSet: CodeSetTypeMap;
}

const initialState: State = {
  codeSet: {}
};

export function reducer(state: State = initialState, action: CodeSetActions) {
  switch (action.type) {
    case CodeSetActionType.LoadSuccess: {
      return {...state, codeSet: action.payload};
    }

    default: {
      return {...state};
    }
  }
}

export const getCodeSet = (state: State) => state.codeSet;
