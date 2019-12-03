import {BulkApprovalEntry, EntryStatus} from '@app/model/decision/bulk-approval-entry';
import {BulkApprovalActions, BulkApprovalActionType} from '../actions/bulk-approval-actions';
import {Dictionary, toDictionary, upsert} from '@util/object.util';
import {OperationStatus} from '@model/common/operation-status';

export interface State {
  loading: boolean;
  entries: BulkApprovalEntry[];
  status: OperationStatus;
  entryStatus: Dictionary<EntryStatus>;
}

const initialState: State = {
  loading: false,
  entries: [],
  status: OperationStatus.NOT_STARTED,
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
        status: OperationStatus.NOT_STARTED,
        entryStatus: {}
      };
    }

    case BulkApprovalActionType.Approve: {
      const entryStatus = action.payload.map((entry) => ({id: entry.id, status: OperationStatus.PENDING}));

      return {
        ...state,
        status: OperationStatus.PENDING,
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
        status: OperationStatus.SUCCESS
      };
    }

    default: {
      return state;
    }
  }
}

export const getLoading = (state: State) => state.loading;
export const getEntries = (state: State) => state.entries;
export const getStatus = (state: State) => state.status;
export const getEntryStatus = (state: State) => state.entryStatus;
