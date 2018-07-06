import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import * as fromCustomerSearch from '../../customerregistry/reducers/customer-search-reducer';
import * as fromContactSearch from '../../customerregistry/reducers/contact-search-reducer';
import * as fromRoot from '../../allu/reducers/index';

export interface CustomerState {
  customerSearch: fromCustomerSearch.State;
  contactSearch: fromContactSearch.State;
}

export interface State extends fromRoot.State {
  customer: CustomerState;
}

export const reducers: ActionReducerMap<CustomerState> = {
  customerSearch: fromCustomerSearch.reducer,
  contactSearch: fromContactSearch.reducer,
};

export const getCustomerState = createFeatureSelector<CustomerState>('customer');

// Customer selectors
export const getCustomerSearchState = createSelector(
  getCustomerState,
  (state: CustomerState) => state.customerSearch
);

export const getMatchingCustomers = createSelector(
  getCustomerSearchState,
  fromCustomerSearch.getMatching
);

export const getLoading = createSelector(
  getCustomerSearchState,
  fromCustomerSearch.getLoading
);

// Contact selectors
export const getContactSearchState = createSelector(
  getCustomerState,
  (state: CustomerState) => state.contactSearch
);

export const getAvailableContacts = createSelector(
  getContactSearchState,
  fromContactSearch.getAvailable
);

export const getMatchingContacts = createSelector(
  getContactSearchState,
  fromContactSearch.getMatching
);

export const getContactsLoading = createSelector(
  getContactSearchState,
  fromContactSearch.getLoading
);

export const getContactsLoaded = createSelector(
  getContactSearchState,
  fromContactSearch.getLoaded
);
