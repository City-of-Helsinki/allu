import {DistributionEntryForm} from '../distribution/distribution-list/distribution-entry-form';
export interface ApplicationForm {
  terms?: string;
  communication?: CommunicationForm;
}

export interface CommunicationForm {
  distributionType?: string;
  publicityType?: string;
  distributionRows?: Array<DistributionEntryForm>;
}
