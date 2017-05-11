import {DistributionEntry} from '../common/distribution-entry';
export class DecisionDetails {
  constructor(
    public decisionDistributionList: Array<DistributionEntry> = [],
    public messageBody?: string
  ) {}

  hasEmails(): boolean {
    return this.decisionDistributionList
      .filter(entry => entry.email)
      .length > 0;
  }
}
