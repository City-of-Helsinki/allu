/* eslint-disable @typescript-eslint/no-unsafe-declaration-merging */
import {Note} from '@model/application/note/note';
import {TimePeriod} from '@feature/application/info/time-period';
import {Application} from '@model/application/application';
import {ApplicationForm} from '@feature/application/info/application-form';
import {TimeUtil} from '@util/time.util';

export interface NoteForm extends ApplicationForm {
  validityTimes: TimePeriod;
  recurringEndYear?: number;
  description?: string;
}

export class NoteForm implements ApplicationForm {
  constructor(
    public name: string,
    public validityTimes: TimePeriod,
    public recurringEndYear?: number,
    public description?: string
  ) {}
}

export function to(form: NoteForm): Note {
  const note = new Note();
  note.description = form.description;
  return note;
}

export function from(application: Application): NoteForm {
  const note = <Note>application.extension || new Note();
  return {
    name: application.name,
    validityTimes: new TimePeriod(application.startTime, application.endTime),
    recurringEndYear: TimeUtil.yearFromDate(application.recurringEndTime),
    description: note.description
  };
}

