import {DistributionEntryForm} from '../distribution/distribution-list/distribution-entry-form';
import {CustomerWithContactsForm} from '../../customerregistry/customer/customer-with-contacts.form';
export interface ApplicationForm {
  applicant?: CustomerWithContactsForm;
  contractor?: CustomerWithContactsForm;
  propertyDeveloper?: CustomerWithContactsForm;
  representative?: CustomerWithContactsForm;
  terms?: string;
  calculatedPrice?: number;
  priceOverride?: number;
  priceOverrideReason?: string;
  communication?: CommunicationForm;
}

export interface CommunicationForm {
  distributionType?: string;
  publicityType?: string;
  distributionRows?: Array<DistributionEntryForm>;
}
