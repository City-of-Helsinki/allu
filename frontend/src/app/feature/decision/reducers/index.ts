import * as fromDecision from './decision-reducer';
import * as fromContract from './contract-reducer';
import * as fromDocument from './document-reducer';
import * as fromApprovalDocument from './approval-document-reducer';
import * as fromRoot from '@feature/allu/reducers';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {Decision} from '@model/decision/Decision';
import {Contract} from '@model/contract/contract';
import {ApprovalDocument, ApprovalDocumentType} from '@model/decision/approval-document';
import {InjectionToken} from '@angular/core';

export interface DecisionState {
  decision: fromDecision.State;
  contract: fromContract.State;
  document: fromDocument.State;
  operationalConditionApproval: fromApprovalDocument.State;
  workFinishedApproval: fromApprovalDocument.State;
}

export interface State extends fromRoot.State {
  decision: DecisionState;
}

export const reducers: ActionReducerMap<DecisionState> = {
  decision: fromDecision.reducer,
  contract: fromContract.reducer,
  document: fromDocument.reducer,
  operationalConditionApproval: fromApprovalDocument.reducerFor(ApprovalDocumentType.OPERATIONAL_CONDITION),
  workFinishedApproval: fromApprovalDocument.reducerFor(ApprovalDocumentType.WORK_FINISHED)
};

export const reducersToken = new InjectionToken<ActionReducerMap<State>>('Decision reducers');

export const reducersProvider = [
  { provide: reducersToken, useValue: reducers }
];

export const getDecisionState = createFeatureSelector<DecisionState>('decision');

export const getDecisionEntitiesState = createSelector(
  getDecisionState,
  (state: DecisionState) => state.decision
);

export const getDecisionLoading = createSelector(
  getDecisionEntitiesState,
  fromDecision.getLoading
);

export const getDecision = createSelector(
  getDecisionEntitiesState,
  fromDecision.getDecision
);

export const getDecisionPdf = createSelector(
  getDecision,
  (decision: Decision) => decision ? decision.pdf : undefined
);

export const getContractEntitiesState = createSelector(
  getDecisionState,
  (state: DecisionState) => state.contract
);

export const getContractLoading = createSelector(
  getContractEntitiesState,
  fromContract.getLoading
);

export const getContract = createSelector(
  getContractEntitiesState,
  fromContract.getContract
);

export const getContractPdf = createSelector(
  getContract,
  (contract: Contract) => contract ? contract.pdf : undefined
);

export const getDocumentEntitiesState = createSelector(
  getDecisionState,
  (state: DecisionState) => state.document
);

export const getTab = createSelector(
  getDocumentEntitiesState,
  fromDocument.getTab
);

/**
 * Operational condition document selectors
 */
export const getOperationalConditionApprovalEntitiesState = createSelector(
  getDecisionState,
  (state: DecisionState) => state.operationalConditionApproval
);

export const getOperationalConditionApprovalLoading = createSelector(
  getOperationalConditionApprovalEntitiesState,
  fromApprovalDocument.getLoading
);

export const getOperationalConditionApproval = createSelector(
  getOperationalConditionApprovalEntitiesState,
  fromApprovalDocument.getDocument
);

export const getOperationalConditionApprovalPdf = createSelector(
  getOperationalConditionApproval,
  (document: ApprovalDocument) => document ? document.pdf : undefined
);

/**
 * Work finished document selectors
 */
export const getWorkFinishedApprovalEntitiesState = createSelector(
  getDecisionState,
  (state: DecisionState) => state.workFinishedApproval
);

export const getWorkFinishedApprovaLoading = createSelector(
  getWorkFinishedApprovalEntitiesState,
  fromApprovalDocument.getLoading
);

export const getWorkFinishedApproval = createSelector(
  getWorkFinishedApprovalEntitiesState,
  fromApprovalDocument.getDocument
);

export const getWorkFinishedApprovalPdf = createSelector(
  getWorkFinishedApproval,
  (document: ApprovalDocument) => document ? document.pdf : undefined
);
