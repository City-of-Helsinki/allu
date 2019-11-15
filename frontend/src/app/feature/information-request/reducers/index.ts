import * as fromInformationRequest from './information-request-reducer';
import * as fromInformationRequestResponse from './information-request-response-reducer';
import * as fromInformationRequestResult from './information-request-result-reducer';
import * as fromInformationRequestSummary from './information-request-summary-reducer';
import * as fromRoot from '../../allu/reducers';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {CustomerWithContacts} from '../../../model/customer/customer-with-contacts';
import {CustomerRoleType} from '../../../model/customer/customer-role-type';
import {InformationRequest} from '@model/information-request/information-request';
import {Dictionary} from '@ngrx/entity';
import {InformationRequestStatus} from '@model/information-request/information-request-status';
import {NumberUtil} from '@util/number.util';

export interface InformationRequestState {
  request: fromInformationRequest.State;
  response: fromInformationRequestResponse.State;
  result: fromInformationRequestResult.State;
  summary: fromInformationRequestSummary.State;
}

export interface State extends fromRoot.State {
  informationRequest: InformationRequestState;
}

export const reducers: ActionReducerMap<InformationRequestState> = {
  request: fromInformationRequest.reducer,
  response: fromInformationRequestResponse.reducer,
  result: fromInformationRequestResult.reducer,
  summary: fromInformationRequestSummary.reducer
};

export const getInformationRequestState = createFeatureSelector<InformationRequestState>('informationRequest');

export const getInformationRequestEntityState = createSelector(
  getInformationRequestState,
  (state: InformationRequestState) => state.request
);

// Information request selectors
export const getInformationRequestEntities = createSelector(
  getInformationRequestEntityState,
  fromInformationRequest.selectRequestEntities
);

export const getInformationRequest = (id: number) => createSelector(
  getInformationRequestEntities,
  (requests: Dictionary<InformationRequest>) => requests[id]
);

export const getInformationRequestLoading = createSelector(
  getInformationRequestEntityState,
  fromInformationRequest.getRequestLoading
);

export const getActiveInformationRequestId = createSelector(
  getInformationRequestEntityState,
  fromInformationRequest.getActive
);

export const getActiveInformationRequest = createSelector(
  getInformationRequestEntities,
  getActiveInformationRequestId,
  (requests: Dictionary<InformationRequest>, active: number) => NumberUtil.isDefined(active) ? requests[active] : undefined
);

export const getActiveInformationRequestResponsePending = createSelector(
  getActiveInformationRequest,
  (request: InformationRequest) => request ? request.status === InformationRequestStatus.RESPONSE_RECEIVED : false
);

// Information request response selectors
export const getInformationRequestResponseEntityState = createSelector(
  getInformationRequestState,
  (state: InformationRequestState) => state.response
);

export const getInformationRequestResponse = (requestId: number) => createSelector(
  getInformationRequestResponseEntityState,
  fromInformationRequestResponse.getResponse(requestId)
);

export const getInformationRequestResponseLoading = createSelector(
  getInformationRequestResponseEntityState,
  fromInformationRequestResponse.getLoading
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

// Summary selectors
export const getSummaryState = createSelector(
  getInformationRequestState,
  state => state.summary
);

export const getSummaries = createSelector(
  getSummaryState,
  fromInformationRequestSummary.getSummaries
);

export const getSummariesLoading = createSelector(
  getSummaryState,
  fromInformationRequestSummary.getLoading
);

export const getSummariesLoaded = createSelector(
  getSummaryState,
  fromInformationRequestSummary.getLoaded
);
