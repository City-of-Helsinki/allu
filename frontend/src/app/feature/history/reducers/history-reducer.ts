import {ChangeHistoryItem} from '@model/history/change-history-item';
import {HistoryActions, HistoryActionType} from '../actions/history-actions';
import {ApplicationStatus} from '@model/application/application-status';

export interface State {
  loading: boolean;
  history: ChangeHistoryItem[];
  statusHistory: ApplicationStatus[];
  fieldsVisible: boolean;
}

export const initialState: State = {
  loading: false,
  history: [],
  statusHistory: [],
  fieldsVisible: false
};

export function reducer(state: State = initialState, action: HistoryActions) {
  switch (action.type) {
    case HistoryActionType.Load:
    case HistoryActionType.LoadByTargetId: {
      return {
        ...state,
        history: [],
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

    case HistoryActionType.SetFieldsVisible: {
      return {
        ...state,
        fieldsVisible: action.payload
      };
    }

    case HistoryActionType.LoadStatus: {
      return {
        ...state,
        statusHistory: [],
      };
    }

    case HistoryActionType.LoadStatusComplete: {
      return {
        ...state,
        statusHistory: action.error ? [] : action.payload
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getHistory = (state: State) => state.history;

export const getFieldsVisible = (state: State) => state.fieldsVisible;

export const getLoading = (state: State) => state.loading;

export const getStatusHistory = (state: State) => state.statusHistory;
