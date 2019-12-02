import {BulkApprovalEntry} from '@app/model/decision/bulk-approval-entry';
import {BulkApprovalActions, BulkApprovalActionType} from '../actions/bulk-approval-actions';

export interface State {
  loading: boolean;
  entries: BulkApprovalEntry[];
  approving: boolean;
  approved: number[];
  failed: number[];
}

const initialState: State = {
  loading: false,
  entries: [],
  approving: false,
  approved: [],
  failed: []
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
        entries: action.payload.entries,
        approved: [],
        failed: []
      };
    }

    case BulkApprovalActionType.Approve: {
      return {
        ...state,
        approving: true
      };
    }

    case BulkApprovalActionType.ApproveEntryComplete: {
      const success = !action.payload.error;
      return {
        ...state,
        approved: success ? state.approved.concat(action.payload.id) : state.approved,
        failed: !success ? state.failed.concat(action.payload.id) : state.failed
      };
    }

    case BulkApprovalActionType.ApproveComplete: {
      return {
        ...state,
        approving: false
      };
    }

    default: {
      return state;
    }
  }
}

export const getLoading = (state: State) => state.loading;
export const getEntries = (state: State) => state.entries;
export const getApproving = (state: State) => state.approving;
export const getApproved = (state: State) => state.approved;
export const getFailed = (state: State) => state.failed;
