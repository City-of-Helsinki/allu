import {ApplicantForm} from '../applicant/applicant.form';
import {EventDetailsForm} from './details/event-details.form';
import {Contact} from '../../../../model/application/contact';
import {ApplicationForm} from '../application-form';

export interface EventForm extends ApplicationForm {
  applicant: ApplicantForm;
  event: EventDetailsForm;
  contacts: Array<Contact>;
}
