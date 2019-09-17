import {ActionReducerMap, createFeatureSelector, createSelector, MemoizedSelector} from '@ngrx/store';
import * as fromCustomerSearch from '@feature/customerregistry/reducers/customer-search-reducer';
import * as fromContactSearch from '@feature/customerregistry/reducers/contact-search-reducer';
import * as fromRoot from '@feature/allu/reducers';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {InjectionToken} from '@angular/core';
import {Customer} from '@model/customer/customer';
import {Page} from '@model/common/page';
import {createCustomerSelectors} from '@feature/customerregistry/reducers/customer-search-reducer';
import {createContactSelectors} from '@feature/customerregistry/reducers/contact-search-reducer';

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
  getMatching: getMatchingCustomers,
  getMatchingList: getMatchingCustomerList,
  getLoading: getCustomersLoading,
  getSearch: getCustomerSearch,
  getSort: getCustomerSort,
  getPageRequest: getCustomerPageRequest,
  getSelectedId: getSelectedCustomerId,
  getSelected: getSelectedCustomer,

} = createCustomerSelectors(getCustomerSearchState);

export const {
  getAvailable: getAvailableContacts,
  getMatching: getMatchingContacts,
  getLoading: getContactsLoading,
  getLoaded: getContactsLoaded
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
