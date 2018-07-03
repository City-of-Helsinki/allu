import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import * as fromCustomerSearch from '../../customerregistry/reducers/customer-search-reducer';
import * as fromRoot from '../../allu/reducers/index';

export interface CustomerState {
  search: fromCustomerSearch.State;
}

export interface State extends fromRoot.State {
  customer: CustomerState;
}

export const reducers: ActionReducerMap<CustomerState> = {
  search: fromCustomerSearch.reducer
};

export const getCustomerState = createFeatureSelector<CustomerState>('customer');

// Customer selectors
export const getCustomerSearchState = createSelector(
  getCustomerState,
  (state: CustomerState) => state.search
);

export const getMatchingCustomers = createSelector(
  getCustomerSearchState,
  fromCustomerSearch.getMatchingCustomers
);

export const getLoading = createSelector(
  getCustomerSearchState,
  fromCustomerSearch.getLoading
);

export const getMatchingContacts = createSelector(
  getCustomerSearchState,
  fromCustomerSearch.getMatchingContacts
);
