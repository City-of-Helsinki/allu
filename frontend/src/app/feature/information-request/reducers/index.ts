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

export const getInformationRequestEntityState = createSelector(
  getInformationRequestState,
  (state: InformationRequestState) => state.request
);

// Information request selectors
export const getInformationRequest = createSelector(
  getInformationRequestEntityState,
  fromInformationRequest.getRequest
);

export const getInformationRequestLoading = createSelector(
  getInformationRequestEntityState,
  fromInformationRequest.getResponseLoading
);

// Information request response selectors
export const getInformationRequestResponse = createSelector(
  getInformationRequestEntityState,
  fromInformationRequest.getResponse
);

export const getInformationRequestResponseLoading = createSelector(
  getInformationRequestEntityState,
  fromInformationRequest.getResponseLoading
);

export const getInformationRequestResponsePending = createSelector(
  getInformationRequestEntityState,
  fromInformationRequest.getResponsePending
);

// Information request result selectors
export const getInformationRequestResultState = createSelector(
  getInformationRequestState,
  (state: InformationRequestState) => state.result
);

export const getResultApplication = createSelector(
  getInformationRequestResultState,
  fromInformationRequestResult.getApplication
);

export const getApplicant = createSelector(
  getInformationRequestResultState,
  fromInformationRequestResult.getApplicant
);

export const getRepresentative = createSelector(
  getInformationRequestResultState,
  fromInformationRequestResult.getRepresentative
);

export const getPropertyDeveloper = createSelector(
  getInformationRequestResultState,
  fromInformationRequestResult.getPropertyDeveloper
);

export const getContractor = createSelector(
  getInformationRequestResultState,
  fromInformationRequestResult.getContractor
);

export const getResultContacts = createSelector(
  getInformationRequestResultState,
  fromInformationRequestResult.getContacts
);

export const getResultKindsWithSpecifiers = createSelector(
  getInformationRequestResultState,
  fromInformationRequestResult.getKindsWithSpecifiers
);

export const getResultInvoicingCustomer = createSelector(
  getInformationRequestResultState,
  fromInformationRequestResult.getInvoicingCustomer
);

export const useCustomerForInvoicing = createSelector(
  getInformationRequestResultState,
  fromInformationRequestResult.useCustomerForInvoicing
);

export const getOtherInfo = createSelector(
  getInformationRequestResultState,
  fromInformationRequestResult.getOtherInfo
);

export const getResultData = createSelector(
  getInformationRequestResultState,
  state => state
);
