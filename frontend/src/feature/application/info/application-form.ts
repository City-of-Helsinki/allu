import {DistributionEntryForm} from '../distribution-list/distribution-entry-form';
export interface ApplicationForm {
  terms?: string;
  communication?: CommunicationForm;
}

export interface CommunicationForm {
  communicationByEmail?: boolean;
  publicityType?: string;
  distributionRows?: Array<DistributionEntryForm>;
}
