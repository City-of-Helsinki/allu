import {EventDetailsForm} from './details/event-details.form';
import {ApplicationForm} from '../application-form';
import {CustomerWithContactsForm} from '../../../customerregistry/customer/customer-with-contacts.form';

export interface EventForm extends ApplicationForm {
  applicant: CustomerWithContactsForm;
  event: EventDetailsForm;
}
