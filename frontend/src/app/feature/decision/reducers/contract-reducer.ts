import {Contract} from '@model/contract/contract';
import {ContractActions, ContractActionType} from '@feature/decision/actions/contract-actions';

export interface State {
  loading: boolean;
  contract: Contract;
}

const initialState: State = {
  loading: false,
  contract: undefined
};

export function reducer(state: State = initialState, action: ContractActions) {
  switch (action.type) {
    case ContractActionType.Load:
    case ContractActionType.CreateProposal:
    case ContractActionType.Approve: {
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

    default: {
      return {...state};
    }
  }
}

export const getLoading = (state: State) => state.loading;

export const getContract = (state: State) => state.contract;
