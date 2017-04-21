import {BackendDecisionDetails} from '../backend-model/backend-decision-details';
import {DecisionDetails} from '../../model/decision/decision-details';
import {DistributionMapper} from './distribution-mapper';

export class DecisionDetailsMapper {
  static mapBackend(backendDecision: BackendDecisionDetails): DecisionDetails {
  return new DecisionDetails(
      DistributionMapper.mapBackendList(backendDecision.decisionDistributionList),
      backendDecision.messageBody
    );
  }

  static mapFrontend(decision: DecisionDetails): BackendDecisionDetails {
    return (decision) ?
      {
        decisionDistributionList: DistributionMapper.mapFrontendList(decision.decisionDistributionList),
        messageBody: decision.messageBody
      }
      : undefined;
  }
}
