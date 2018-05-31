import {ChangeHistoryItem} from '../../../model/history/change-history-item';
import {HistoryActions, HistoryActionType} from '../actions/history-actions';

export interface State {
  loading: boolean;
  history: ChangeHistoryItem[];
}

export const initialState: State = {
  loading: false,
  history: []
};

export function reducer(state: State = initialState, action: HistoryActions) {
  switch (action.type) {
    case HistoryActionType.Load: {
      return {
        ...state,
        loading: true
      };
    }

    case HistoryActionType.LoadSuccess: {
      return {
        ...state,
        loading: false,
        history: action.payload
      };
    }

    case HistoryActionType.LoadFailed: {
      return {
        ...state,
        loading: false,
        history: []
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getHistory = (state: State) => state.history;
