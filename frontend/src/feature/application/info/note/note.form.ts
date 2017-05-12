import {Note} from '../../../../model/application/note/note';
import {TimePeriod} from '../time-period';
import {Application} from '../../../../model/application/application';
import {ApplicantForm} from '../applicant/applicant.form';
import {Contact} from '../../../../model/application/contact';
import {ApplicationForm} from '../application-form';

export class NoteForm implements ApplicationForm {
  constructor(
    public name: string,
    public validityTimes: TimePeriod,
    public recurringEndYear?: number,
    public description?: string,

    public applicant?: ApplicantForm,
    public contacts?: Array<Contact>
  ) {}

  static to(form: NoteForm): Note {
    return new Note(form.description);
  }

  static from(application: Application): NoteForm {
    let note = <Note>application.extension || new Note();
    return new NoteForm(
      application.name,
      new TimePeriod(application.uiStartTime, application.uiEndTime),
      application.recurringEndYear,
      note.description
    );
  }
}

