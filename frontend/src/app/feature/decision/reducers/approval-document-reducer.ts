import {ApplicationActions, ApplicationActionType} from '@feature/application/actions/application-actions';
import {ApprovalDocumentActions, ApprovalDocumentActionType} from '@feature/decision/actions/approval-document.actions';
import {ApprovalDocument, ApprovalDocumentType} from '@model/decision/approval-document';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

export interface State {
  loading: boolean;
  document: ApprovalDocument;
}

const initialState: State = {
  loading: false,
  document: undefined
};

export function reducer(state: State = initialState, action: ApprovalDocumentActions | ApplicationActions) {
  switch (action.type) {
    case ApprovalDocumentActionType.Load: {
      return {
        ...state,
        loading: true
      };
    }

    case ApprovalDocumentActionType.LoadSuccess: {
      return {
        ...state,
        loading: false,
        document: action.payload
      };
    }

    case ApprovalDocumentActionType.LoadFailed: {
      return {
        ...state,
        loading: false
      };
    }

    case ApplicationActionType.LoadSuccess: {
      return {
        ...state,
        document: undefined
      };
    }

    default: {
      return {...state};
    }
  }
}

export function reducerFor(documentType: ApprovalDocumentType) {
  return function(state: State = initialState, action: ApprovalDocumentActions | ApplicationActions) {
    switch (action.type) {
      case ApplicationActionType.LoadSuccess: {
        return reducer(state, action);
      }

      case ApprovalDocumentActionType.Load:
      case ApprovalDocumentActionType.LoadSuccess:
      case ApprovalDocumentActionType.LoadFailed: {
        if (action.documentType === documentType) {
          return reducer(state, action);
        } else {
          return state;
        }
      }

      default: {
        return state;
      }
    }
  };
}

export const getLoading = (state: State) => state.loading;

export const getDocument = (state: State) => state.document;
