import {BulkApprovalEntry, EntryStatus} from '@app/model/decision/bulk-approval-entry';
import {BulkApprovalActions, BulkApprovalActionType} from '../actions/bulk-approval-actions';
import {Dictionary, toDictionary, upsert} from '@util/object.util';
import {OperationStatus} from '@model/common/operation-status';

export interface State {
  loading: boolean;
  entries: BulkApprovalEntry[];
  approving: boolean;
  entryStatus: Dictionary<EntryStatus>;
}

const initialState: State = {
  loading: false,
  entries: [],
  approving: false,
  entryStatus: {}
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
        entryStatus: {}
      };
    }

    case BulkApprovalActionType.Approve: {
      const entryStatus = action.payload.map((entry) => ({id: entry.id, status: OperationStatus.PENDING}));

      return {
        ...state,
        approving: true,
        entryStatus: toDictionary<EntryStatus>(entryStatus, entry => entry.id)
      };
    }

    case BulkApprovalActionType.ApproveEntryComplete: {
      return {
        ...state,
        entryStatus: upsert(state.entryStatus, action.payload.id, {
          id: action.payload.id,
          status: action.payload.error ? OperationStatus.FAIL : OperationStatus.SUCCESS,
          error: action.payload.error
        })
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
export const getEntryStatus = (state: State) => state.entryStatus;
