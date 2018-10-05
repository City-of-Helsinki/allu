import * as fromDecision from './decision-reducer';
import * as fromContract from './contract-reducer';
import * as fromDocument from './document-reducer';
import * as fromRoot from '@feature/allu/reducers';
import {ActionReducerMap, createFeatureSelector, createSelector} from '@ngrx/store';
import {Decision} from '@model/decision/Decision';
import {Contract} from '@model/contract/contract';
import {DecisionTab} from '@feature/decision/documents/decision-tab';

export interface DecisionState {
  decision: fromDecision.State;
  contract: fromContract.State;
  document: fromDocument.State;
}

export interface State extends fromRoot.State {
  decision: DecisionState;
}

export const reducers: ActionReducerMap<DecisionState> = {
  decision: fromDecision.reducer,
  contract: fromContract.reducer,
  document: fromDocument.reducer
};

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
