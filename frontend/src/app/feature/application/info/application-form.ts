import {DistributionEntryForm} from '../distribution/distribution-list/distribution-entry-form';
import {CustomerWithContactsForm} from '../../customerregistry/customer/customer-with-contacts.form';

export interface ApplicationForm {
  name?: string;
  applicant?: CustomerWithContactsForm;
  contractor?: CustomerWithContactsForm;
  propertyDeveloper?: CustomerWithContactsForm;
  representative?: CustomerWithContactsForm;
  invoiceRecipientId?: number;
  terms?: string;
  calculatedPrice?: number;
  communication?: CommunicationForm;
}

export interface CommunicationForm {
  distributionType?: string;
  publicityType?: string;
  distributionRows?: Array<DistributionEntryForm>;
}
