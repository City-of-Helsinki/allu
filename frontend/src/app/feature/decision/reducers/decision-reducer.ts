import {Decision} from '@model/decision/Decision';
import {DecisionActions, DecisionActionType} from '@feature/decision/actions/decision-actions';
import {ApplicationActions, ApplicationActionType} from '@feature/application/actions/application-actions';

export interface State {
  loading: boolean;
  decision: Decision;
}

const initialState: State = {
  loading: false,
  decision: undefined,
};

export function reducer(state: State = initialState, action: DecisionActions | ApplicationActions) {
  switch (action.type) {
    case DecisionActionType.Load: {
      return {
        ...state,
        loading: true
      };
    }

    case DecisionActionType.LoadSuccess: {
      return {
        ...state,
        loading: false,
        decision: action.payload
      };
    }

    case DecisionActionType.LoadFailed: {
      return {
        ...state,
        loading: false
      };
    }

    case ApplicationActionType.LoadSuccess: {
      return {
        ...state,
        decision: undefined
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getLoading = (state: State) => state.loading;

export const getDecision = (state: State) => state.decision;
