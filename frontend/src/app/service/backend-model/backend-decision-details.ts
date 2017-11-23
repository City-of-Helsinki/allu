import {BackendDistributionEntry} from './backend-distribution-entry';

export interface BackendDecisionDetails {
  decisionDistributionList: Array<BackendDistributionEntry>;
  messageBody: string;
}
