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
  representativeSearch: fromCustomerSearch.State;
  representativeContactSearch: fromContactSearch.State;
  propertyDeveloperSearch: fromCustomerSearch.State;
  propertyDeveloperContactSearch: fromContactSearch.State;
  contractorSearch: fromCustomerSearch.State;
  contractorContactSearch: fromContactSearch.State;
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
  representativeSearch: fromCustomerSearch.createReducerFor(ActionTargetType.Representative),
  representativeContactSearch: fromContactSearch.createReducerFor(ActionTargetType.Representative),
  propertyDeveloperSearch: fromCustomerSearch.createReducerFor(ActionTargetType.PropertyDeveloper),
  propertyDeveloperContactSearch: fromContactSearch.createReducerFor(ActionTargetType.PropertyDeveloper),
  contractorSearch: fromCustomerSearch.createReducerFor(ActionTargetType.Contractor),
  contractorContactSearch: fromContactSearch.createReducerFor(ActionTargetType.Contractor),
  invoicingCustomerSearch: fromCustomerSearch.createReducerFor(ActionTargetType.InvoicingCustomer),
  invoicingCustomerContactSearch: fromContactSearch.createReducerFor(ActionTargetType.InvoicingCustomer)
};

export const reducersToken = new InjectionToken<ActionReducerMap<State>>('Customer reducers');

export const reducersProvider = [
  { provide: reducersToken, useValue: reducers }
];

export const getCustomerState = createFeatureSelector<CustomerState>('customer');

export function createCustomerStateSelector(selector: (state: CustomerState) => fromCustomerSearch.State) {
  return createSelector(getCustomerState, selector);
}

export function createContactsStateSelector(selector: (state: CustomerState) => fromContactSearch.State) {
  return createSelector(getCustomerState, selector);
}

function createCustomerSelectors(getState: MemoizedSelector<object, fromCustomerSearch.State>) {
  return {
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

export const getCustomerSearchState = createCustomerStateSelector((state: CustomerState) => state.customerSearch);
export const getContactSearchState = createContactsStateSelector((state: CustomerState) => state.contactSearch);
export const getApplicantSearchState = createCustomerStateSelector((state: CustomerState) => state.applicantSearch);
export const getApplicantContactSearchState = createContactsStateSelector((state: CustomerState) => state.applicantContactSearch);
export const getRepresentativeSearchState = createCustomerStateSelector((state: CustomerState) => state.representativeSearch);
export const getRepresentativeContactSearchState = createContactsStateSelector((state: CustomerState) => state.representativeContactSearch);
export const getPropertyDeveloperSearchState = createCustomerStateSelector((state: CustomerState) => state.propertyDeveloperSearch);
export const getPropertyDeveloperContactSearchState = createContactsStateSelector(
  (state: CustomerState) => state.propertyDeveloperContactSearch);
export const getContractorSearchState = createCustomerStateSelector((state: CustomerState) => state.contractorSearch);
export const getContractorContactSearchState = createContactsStateSelector((state: CustomerState) => state.contractorContactSearch);
export const getInvoicingCustomerSearchState = createCustomerStateSelector((state: CustomerState) => state.invoicingCustomerSearch);

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

export const getApplicantSelectors = createCustomerSelectors(getApplicantSearchState);
export const getApplicantContactsSelectors = createContactSelectors(getApplicantContactSearchState);

export const getRepresentativeSelectors = createCustomerSelectors(getRepresentativeSearchState);
export const getRepresentativeContactsSelectors = createContactSelectors(getRepresentativeContactSearchState);

export const getPropertyDeveloperSelectors = createCustomerSelectors(getPropertyDeveloperSearchState);
export const getPropertyDeveloperContactsSelectors = createContactSelectors(getPropertyDeveloperContactSearchState);

export const getContractorSelectors = createCustomerSelectors(getContractorSearchState);
export const getContractorContactsSelectors = createContactSelectors(getContractorContactSearchState);

export const getInvoicingCustomerSelectors = createCustomerSelectors(getInvoicingCustomerSearchState);
