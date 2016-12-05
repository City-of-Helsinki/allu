import {ApplicantForm} from '../applicant/applicant.form';
import {EventDetailsForm} from './details/event-details.form';
import {Contact} from '../../../../model/application/contact';

export interface EventForm {
  applicant: ApplicantForm;
  event: EventDetailsForm;
  contacts: Array<Contact>;
}
