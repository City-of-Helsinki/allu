import {TerminationInfo} from '@feature/decision/termination/termination-info';
import {TerminationActions, TerminationActionType} from '@feature/decision/actions/termination-actions';
import {ApplicationActions, ApplicationActionType} from '@feature/application/actions/application-actions';
import {TerminationDocument} from '@feature/decision/termination/TerminationDocument';

export interface State {
  loading: boolean;
  termination: TerminationInfo;
  terminationDocument: TerminationDocument;
}

const initialState: State = {
  loading: false,
  termination: undefined,
  terminationDocument: undefined
};

export function reducer(state: State = initialState, action: TerminationActions | ApplicationActions) {
  switch (action.type) {
    case TerminationActionType.LoadInfo: {
      return {
        ...state,
        loading: true
      };
    }

    case TerminationActionType.LoadInfoSuccess: {
      return {
        ...state,
        loading: false,
        termination: action.payload
      };
    }

    case TerminationActionType.LoadInfoFailed: {
      return {
        ...state,
        loading: false
      };
    }


    case TerminationActionType.LoadDocument: {
      return {
        ...state,
        loading: true
      };
    }

    case TerminationActionType.LoadDocumentSuccess: {
      return {
        ...state,
        loading: false,
        terminationDocument: action.payload
      };
    }

    case TerminationActionType.LoadDocumentFailed: {
      return {
        ...state,
        loading: false
      };
    }


    case TerminationActionType.Terminate: {
      return {
        ...state,
        loading: true
      };
    }

    case TerminationActionType.TerminationDraftSuccess: {
      return {
        ...state,
        loading: false,
        termination: action.payload,
        terminationDocument: undefined
      };
    }

    case TerminationActionType.TerminationDraftFailed: {
      return {
        ...state,
        loading: false
      };
    }

    case TerminationActionType.MoveTerminationToDecision: {
      return {
        ...state,
        loading: true
      };
    }

    case TerminationActionType.MoveTerminationToDecisionSuccess: {
      return {
        ...state,
        loading: false
      };
    }

    case TerminationActionType.MoveTerminationToDecisionFailed: {
      return {
        ...state,
        loading: false
      };
    }

    case ApplicationActionType.LoadSuccess: {
      return {
        ...state,
        terminationDocument: undefined
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getLoading = (state: State) => state.loading;

export const getTermination = (state: State) => state.termination;

export const getTerminationDocument = (state: State) => state.terminationDocument;
