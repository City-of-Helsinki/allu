import {Note} from '../../../../model/application/note/note';
import {TimePeriod} from '../time-period';
import {Application} from '../../../../model/application/application';
import {ApplicationForm} from '../application-form';
import {CustomerWithContactsForm} from '../../../customerregistry/customer/customer-with-contacts.form';

export class NoteForm implements ApplicationForm {
  constructor(
    public name: string,
    public validityTimes: TimePeriod,
    public recurringEndYear?: number,
    public description?: string,
    public applicant?: CustomerWithContactsForm
  ) {}

  static to(form: NoteForm): Note {
    let note = new Note();
    note.description = form.description;
    return note;
  }

  static from(application: Application): NoteForm {
    let note = <Note>application.extension || new Note();
    return new NoteForm(
      application.name,
      new TimePeriod(application.startTime, application.endTime),
      application.recurringEndYear,
      note.description
    );
  }
}

