import {DistributionEntryForm} from '../distribution/distribution-list/distribution-entry-form';
import {CustomerWithContactsForm} from '../../customerregistry/customer/customer-with-contacts.form';
import {Validators} from '@angular/forms';
import {Application} from '@model/application/application';

export interface ApplicationForm {
  name?: string;
  applicant?: CustomerWithContactsForm;
  contractor?: CustomerWithContactsForm;
  propertyDeveloper?: CustomerWithContactsForm;
  representative?: CustomerWithContactsForm;
  invoiceRecipientId?: number;
  calculatedPrice?: number;
  communication?: CommunicationForm;
  receivedTime?: Date;
}

export interface CommunicationForm {
  publicityType?: string;
  distributionRows?: Array<DistributionEntryForm>;
}

export function applicationForm(app: Application = new Application()): { [key: string]: any; } {
  return {
    name: [app.name, [Validators.required, Validators.minLength(2)]],
    receivedTime: [app.receivedTime, Validators.required]
  };
}
