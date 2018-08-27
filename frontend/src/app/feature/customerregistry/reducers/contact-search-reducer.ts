import {Contact} from '@model/customer/contact';
import {ContactSearchActions, ContactSearchActionType} from '@feature/customerregistry/actions/contact-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';

export interface State {
  loading: boolean;
  loaded: boolean;
  search: string;
  available: Contact[];
  matching: Contact[];
}

export const initialState: State = {
  loading: false,
  loaded: false,
  search: undefined,
  available: [],
  matching: []
};

function reducer(state: State = initialState, action: ContactSearchActions) {
  switch (action.type) {
    case ContactSearchActionType.LoadByCustomer: {
      return {
        ...state,
        loading: true,
        loaded: false
      };
    }

    case ContactSearchActionType.LoadByCustomerSuccess: {
      return {
        ...state,
        loading: false,
        loaded: true,
        available: action.payload,
        matching: []
      };
    }

    case ContactSearchActionType.LoadByCustomerFailed: {
      return {
        ...state,
        loading: false,
        loaded: true,
        available: [],
        matching: []
      };
    }

    case ContactSearchActionType.Search: {
      const searchTerm = action.payload ? action.payload.toLocaleLowerCase() : '';
      const matching = state.available
        .filter(c => c.name.toLocaleLowerCase().startsWith(searchTerm));

      return {
        ...state,
        matching: matching
      };
    }

    default: {
      return {...state};
    }
  }
}

export function createReducerFor(targetType: ActionTargetType) {
  return function(state: State = initialState, action: ContactSearchActions) {
    if (targetType === action.targetType) {
      return reducer(state, action);
    } else {
      return state;
    }
  };
}

export const getMatching = (state: State) => state.matching;

export const getAvailable = (state: State) => state.available;

export const getLoading = (state: State) => state.loading;

export const getLoaded = (state: State) => state.loaded;
