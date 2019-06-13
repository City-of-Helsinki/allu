import {Contract} from '@model/contract/contract';
import {ContractActions, ContractActionType} from '@feature/decision/actions/contract-actions';
import {ApplicationActions, ApplicationActionType} from '@feature/application/actions/application-actions';

export interface State {
  loading: boolean;
  contract: Contract;
}

const initialState: State = {
  loading: false,
  contract: undefined
};

export function reducer(state: State = initialState, action: ContractActions | ApplicationActions) {
  switch (action.type) {
    case ContractActionType.Load:
    case ContractActionType.CreateProposal:
    case ContractActionType.Approve:
    case ContractActionType.Reject: {
      return {
        ...state,
        loading: true
      };
    }

    case ContractActionType.LoadSuccess:
    case ContractActionType.CreateProposalSuccess:
    case ContractActionType.ApproveSuccess: {
      return {
        ...state,
        loading: false,
        contract: action.payload
      };
    }

    case ContractActionType.LoadFailed:
    case ContractActionType.CreateProposalFailed:
    case ContractActionType.ApproveFailed: {
      return {
        ...state,
        loading: false
      };
    }

    case ContractActionType.RejectSuccess: {
      return {
        ...state,
        loading: false,
        contract: undefined
      };
    }

    case ApplicationActionType.LoadSuccess: {
      return {
        ...state,
        contract: undefined
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getLoading = (state: State) => state.loading;

export const getContract = (state: State) => state.contract;
