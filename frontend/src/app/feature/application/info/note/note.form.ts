import {Note} from '../../../../model/application/note/note';
import {TimePeriod} from '../time-period';
import {Application} from '../../../../model/application/application';
import {ApplicationForm} from '../application-form';
import {TimeUtil} from '../../../../util/time.util';

export class NoteForm implements ApplicationForm {
  constructor(
    public name: string,
    public validityTimes: TimePeriod,
    public recurringEndYear?: number,
    public description?: string
  ) {}

  static to(form: NoteForm): Note {
    const note = new Note();
    note.description = form.description;
    return note;
  }

  static from(application: Application): NoteForm {
    const note = <Note>application.extension || new Note();
    return new NoteForm(
      application.name,
      new TimePeriod(application.startTime, application.endTime),
      TimeUtil.yearFromDate(application.recurringEndTime),
      note.description
    );
  }
}

