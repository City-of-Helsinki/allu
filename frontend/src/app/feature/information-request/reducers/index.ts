import * as fromInformationRequest from './information-request-reducer';
import * as fromInformationRequestResult from './information-request-result-reducer';
import * as fromRoot from '../../allu/reducers';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {CustomerWithContacts} from '../../../model/customer/customer-with-contacts';
import {CustomerRoleType} from '../../../model/customer/customer-role-type';

export interface InformationRequestState {
  request: fromInformationRequest.State;
  result: fromInformationRequestResult.State;
}

export interface State extends fromRoot.State {
  informationRequest: InformationRequestState;
}

export const reducers: ActionReducerMap<InformationRequestState> = {
  request: fromInformationRequest.reducer,
  result: fromInformationRequestResult.reducer
};

export const getInformationRequestState = createFeatureSelector<InformationRequestState>('informationRequest');

export const getInformationRequestResultState = createSelector(
  getInformationRequestState,
  (state: InformationRequestState) => state.result
);

export const getResultApplication = createSelector(
  getInformationRequestResultState,
  fromInformationRequestResult.getApplication
);

export const getResultCustomer = createSelector(
  getInformationRequestResultState,
  fromInformationRequestResult.getCustomer
);

export const getResultContacts = createSelector(
  getInformationRequestResultState,
  fromInformationRequestResult.getContacts
);

// Handle different role types when supported by external api
export const getResultCustomerWithContacts = createSelector(
  getResultCustomer,
  getResultContacts,
  (customer, contacts) => new CustomerWithContacts(CustomerRoleType.APPLICANT, customer, contacts)
);
