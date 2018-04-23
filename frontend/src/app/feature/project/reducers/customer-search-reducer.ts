import {CustomerSearchQuery} from '../../../service/customer/customer-search-query';
import {Customer} from '../../../model/customer/customer';
import {CustomerSearchActions, CustomerSearchActionType} from '../actions/customer-search-actions';
import {Contact} from '../../../model/customer/contact';

export interface State {
  search: CustomerSearchQuery;
  matchingCustomers: Customer[];
  contactSearch: string;
  availableContacts: Contact[];
  matchingContacts: Contact[];
}

const initialState: State = {
  search: undefined,
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
        search: action.payload
      };
    }

    case CustomerSearchActionType.SearchSuccess: {
      return {
        ...state,
        matchingCustomers: action.payload
      };
    }

    case CustomerSearchActionType.SearchFailed: {
      return {
        ...state,
        matchingCustomers: []
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
