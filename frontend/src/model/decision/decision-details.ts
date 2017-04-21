import {DistributionEntry} from '../common/distribution-entry';
export class DecisionDetails {
  constructor(
    public decisionDistributionList: Array<DistributionEntry> = [],
    public messageBody?: string
  ) {}
}
