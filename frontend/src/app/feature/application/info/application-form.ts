import {DistributionEntryForm} from '../distribution/distribution-list/distribution-entry-form';
import {CustomerWithContactsForm} from '../../customerregistry/customer/customer-with-contacts.form';
import {Validators} from '@angular/forms';

export interface ApplicationForm {
  name?: string;
  applicant?: CustomerWithContactsForm;
  contractor?: CustomerWithContactsForm;
  propertyDeveloper?: CustomerWithContactsForm;
  representative?: CustomerWithContactsForm;
  invoiceRecipientId?: number;
  calculatedPrice?: number;
  communication?: CommunicationForm;
}

export interface CommunicationForm {
  publicityType?: string;
  distributionRows?: Array<DistributionEntryForm>;
}

export function applicationForm(): { [key: string]: any; } {
  return {
    name: ['', [Validators.required, Validators.minLength(2)]]
  };
}
