import {ActionReducerMap, createFeatureSelector, createSelector, MemoizedSelector} from '@ngrx/store';
import * as fromCustomerSearch from '../../customerregistry/reducers/customer-search-reducer';
import * as fromContactSearch from '../../customerregistry/reducers/contact-search-reducer';
import * as fromRoot from '../../allu/reducers/index';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {InjectionToken} from '@angular/core';

export interface CustomerState {
  customerSearch: fromCustomerSearch.State;
  contactSearch: fromContactSearch.State;
  applicantSearch: fromCustomerSearch.State;
  applicantContactSearch: fromContactSearch.State;
  invoicingCustomerSearch: fromCustomerSearch.State;
  invoicingCustomerContactSearch: fromContactSearch.State;
}

export interface State extends fromRoot.State {
  customer: CustomerState;
}

export const reducers: ActionReducerMap<CustomerState> = {
  customerSearch: fromCustomerSearch.createReducerFor(ActionTargetType.Customer),
  contactSearch: fromContactSearch.createReducerFor(ActionTargetType.Customer),
  applicantSearch: fromCustomerSearch.createReducerFor(ActionTargetType.Applicant),
  applicantContactSearch: fromContactSearch.createReducerFor(ActionTargetType.Applicant),
  invoicingCustomerSearch: fromCustomerSearch.createReducerFor(ActionTargetType.InvoicingCustomer),
  invoicingCustomerContactSearch: fromContactSearch.createReducerFor(ActionTargetType.InvoicingCustomer)
};

export const reducersToken = new InjectionToken<ActionReducerMap<State>>('Customer reducers');

export const reducersProvider = [
  { provide: reducersToken, useValue: reducers }
];

export const getCustomerState = createFeatureSelector<CustomerState>('customer');

export const getCustomerSearchState = createSelector(
  getCustomerState,
  (state: CustomerState) => state.customerSearch
);

export const getContactSearchState = createSelector(
  getCustomerState,
  (state: CustomerState) => state.contactSearch
);

export const getApplicantSearchState = createSelector(
  getCustomerState,
  (state: CustomerState) => state.applicantSearch
);

export const getApplicantContactSearchState = createSelector(
  getCustomerState,
  (state: CustomerState) => state.applicantContactSearch
);

export const getInvoicingCustomerSearchState = createSelector(
  getCustomerState,
  (state: CustomerState) => state.invoicingCustomerSearch
);

export const getInvoicingCustomerContactSearchState = createSelector(
  getCustomerState,
  (state: CustomerState) => state.invoicingCustomerContactSearch
);

function createCustomerSelectors(getState: MemoizedSelector<object, fromCustomerSearch.State>) {
  return {
    // Customer selectors
    getMatchingCustomers: createSelector(
      getState,
      fromCustomerSearch.getMatching
    ),

    getLoading: createSelector(
      getState,
      fromCustomerSearch.getLoading
    )
  };
}

function createContactSelectors(getState: MemoizedSelector<object, fromContactSearch.State>) {
  return {
    getAvailableContacts: createSelector(
      getState,
      fromContactSearch.getAvailable
    ),

    getMatchingContacts: createSelector(
      getState,
      fromContactSearch.getMatching
    ),

    getContactsLoading: createSelector(
      getState,
      fromContactSearch.getLoading
    ),

    getContactsLoaded: createSelector(
      getState,
      fromContactSearch.getLoaded
    )
  };
}

export const {
  getMatchingCustomers,
  getLoading
} = createCustomerSelectors(getCustomerSearchState);

export const {
  getAvailableContacts,
  getMatchingContacts,
  getContactsLoading,
  getContactsLoaded
} = createContactSelectors(getContactSearchState);

export const {
  getMatchingCustomers: getMatchingApplicants,
  getLoading: getApplicantsLoading
} = createCustomerSelectors(getApplicantSearchState);

export const {
  getAvailableContacts: getAvailableApplicantContacts,
  getMatchingContacts: getMatchingApplicantContacts,
  getContactsLoading: getApplicantContactsLoading,
  getContactsLoaded: getApplicantContactsLoaded
} = createContactSelectors(getApplicantContactSearchState);

export const {
  getMatchingCustomers: getMatchingInvoicingCustomers,
  getLoading: getInvoicingCustomersLoading
} = createCustomerSelectors(getInvoicingCustomerSearchState);

export const {
  getAvailableContacts: getAvailableInvoicingCustomerContacts,
  getMatchingContacts: getMatchingInvoicingCustomerContacts,
  getContactsLoading: getInvoicingCustomerContactsLoading,
  getContactsLoaded: getInvoicingCustomerContactsLoaded
} = createContactSelectors(getInvoicingCustomerContactSearchState);

