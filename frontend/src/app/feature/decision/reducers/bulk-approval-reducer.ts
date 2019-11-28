import {BulkApprovalEntry} from '@app/model/decision/bulk-approval-entry';
import {BulkApprovalActions, BulkApprovalActionType} from '../actions/bulk-approval-actions';

export interface State {
  loading: boolean;
  entries: BulkApprovalEntry[];
}

const initialState: State = {
  loading: false,
  entries: []
};

export function reducer(state: State = initialState, action: BulkApprovalActions) {
  switch (action.type) {
    case BulkApprovalActionType.Load: {
      return {
        ...state,
        loading: true
      };
    }

    case BulkApprovalActionType.LoadComplete: {
      return {
        ...state,
        loading: false,
        entries: action.payload.entries
      };
    }

    default: {
      return state;
    }
  }
}

export const getLoading = (state: State) => state.loading;
export const getEntries = (state: State) => state.entries;
