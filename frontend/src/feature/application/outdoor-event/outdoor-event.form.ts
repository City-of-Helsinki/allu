import {ApplicantForm} from '../applicant/applicant.form';
import {OutdoorEventDetailsForm} from './details/outdoor-event-details.form';
import {Contact} from '../../../model/application/contact';

export interface OutdoorEventForm {
  applicant: ApplicantForm;
  event: OutdoorEventDetailsForm;
  contacts: Array<Contact>;
}
