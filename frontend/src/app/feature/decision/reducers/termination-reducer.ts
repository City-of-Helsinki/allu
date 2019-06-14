import {TerminationInfo} from '@feature/decision/termination/termination-info';
import {TerminationActions, TerminationActionType} from '@feature/decision/actions/termination-actions';
import {ApplicationActions, ApplicationActionType} from '@feature/application/actions/application-actions';

export interface State {
  loading: boolean;
  termination: TerminationInfo;
}

const initialState: State = {
  loading: false,
  termination: undefined,
};

export function reducer(state: State = initialState, action: TerminationActions | ApplicationActions) {
  switch (action.type) {
    case TerminationActionType.Load: {
      return {
        ...state,
        loading: true
      };
    }

    case TerminationActionType.LoadSuccess: {
      return {
        ...state,
        loading: false,
        termination: action.payload
      };
    }

    case TerminationActionType.LoadFailed: {
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
        termination: action.payload
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
        termination: undefined
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getLoading = (state: State) => state.loading;

export const getTermination = (state: State) => state.termination;
