import {CustomerSearchQuery} from '../../../service/customer/customer-search-query';
import {Customer} from '../../../model/customer/customer';
import {Contact} from '../../../model/customer/contact';
import {CustomerSearchActions, CustomerSearchActionType} from '../actions/customer-search-actions';

export interface State {
  search: CustomerSearchQuery;
  loading: boolean;
  matchingCustomers: Customer[];
  contactSearch: string;
  availableContacts: Contact[];
  matchingContacts: Contact[];
}

const initialState: State = {
  search: undefined,
  loading: false,
  matchingCustomers: [],
  contactSearch: undefined,
  availableContacts: [],
  matchingContacts: []
};

export function reducer(state: State = initialState, action: CustomerSearchActions) {
  switch (action.type) {
    case CustomerSearchActionType.Search: {
      return {
        ...state,
        search: action.payload,
        loading: true
      };
    }

    case CustomerSearchActionType.SearchSuccess: {
      return {
        ...state,
        matchingCustomers: action.payload,
        loading: false
      };
    }

    case CustomerSearchActionType.SearchFailed: {
      return {
        ...state,
        matchingCustomers: [],
        loading: false
      };
    }

    case CustomerSearchActionType.LoadContactsSuccess: {
      return {
        ...state,
        availableContacts: action.payload,
        matchingContacts: []
      };
    }

    case CustomerSearchActionType.LoadContactsFailed: {
      return {
        ...state,
        availableContacts: [],
        matchingContacts: []
      };
    }

    case CustomerSearchActionType.SearchContacts: {
      const searchTerm = action.payload ? action.payload.toLocaleLowerCase() : '';
      const matching = state.availableContacts
        .filter(c => c.name.toLocaleLowerCase().startsWith(searchTerm));

      return {
        ...state,
        matchingContacts: matching
      };
    }

    default: {
      return {...state};
    }
  }
}

export const getMatchingCustomers = (state: State) => state.matchingCustomers;

export const getMatchingContacts = (state: State) => state.matchingContacts;

export const getLoading = (state: State) => state.loading;
