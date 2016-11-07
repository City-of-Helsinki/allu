import {ApplicantForm} from '../applicant/applicant.form';
import {Contact} from '../../../../model/application/contact';
import {TimePeriod} from '../time-period';
import {ShortTermRental} from '../../../../model/application/short-term-rental/short-term-rental';
import {Application} from '../../../../model/application/application';

export interface ShortTermRentalForm {
  applicant: ApplicantForm;
  contacts: Array<Contact>;
  details: ShortTermRentalDetailsForm;
}

export class ShortTermRentalDetailsForm {
  constructor()
  constructor(
    name: string,
    description: string,
    area: number,
    rentalTimes: TimePeriod,
    commercial: boolean)
  constructor(
    public name?: string,
    public description?: string,
    public area?: number,
    public rentalTimes?: TimePeriod,
    public commercial?: boolean) {}

  static from(application: Application, rental: ShortTermRental): ShortTermRentalDetailsForm {
    return new ShortTermRentalDetailsForm(
      application.name,
      rental.description,
      application.location.area,
      new TimePeriod(application.uiStartTime, application.uiEndTime),
      rental.commercial
    );
  }

  static to(form: ShortTermRentalDetailsForm): ShortTermRental {
    return new ShortTermRental(form.description, form.commercial);
  }
}
