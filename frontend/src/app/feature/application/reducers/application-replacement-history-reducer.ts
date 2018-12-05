import {ApplicationIdentifier} from '@model/application/application-identifier';
import {
  ApplicationReplacementHistoryActions,
  ApplicationReplacementHistoryActionType
} from '@feature/application/actions/application-replacement-history-actions';

export interface State {
  replacementHistory: ApplicationIdentifier[];
}

const initialState: State = {
  replacementHistory: []
};

export function reducer(state: State = initialState, action: ApplicationReplacementHistoryActions) {
  switch (action.type) {
    case ApplicationReplacementHistoryActionType.LoadSuccess: {
      return {
        ...state,
        replacementHistory: action.payload
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getReplacementHistory = (state: State) => state.replacementHistory;
