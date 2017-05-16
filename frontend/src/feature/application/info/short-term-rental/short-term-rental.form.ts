import {ApplicantForm} from '../applicant/applicant.form';
import {Contact} from '../../../../model/application/contact';
import {TimePeriod} from '../time-period';
import {ShortTermRental} from '../../../../model/application/short-term-rental/short-term-rental';
import {Application} from '../../../../model/application/application';
import {ApplicationForm} from '../application-form';

export class ShortTermRentalForm implements ApplicationForm {
  constructor(
    public name?: string,
    public description?: string,
    public rentalTimes?: TimePeriod,
    public commercial?: boolean,
    public largeSalesArea?: boolean,
    public calculatedPrice?: number,
    public priceOverride?: number,
    public priceOverrideReason?: string,
    public applicant?: ApplicantForm,
    public contacts?: Array<Contact>) {}

  static from(application: Application, rental: ShortTermRental): ShortTermRentalForm {
    return new ShortTermRentalForm(
      application.name,
      rental.description,
      new TimePeriod(application.startTime, application.endTime),
      rental.commercial,
      rental.largeSalesArea,
      application.calculatedPriceEuro,
      application.priceOverrideEuro,
      application.priceOverrideReason
    );
  }

  static to(form: ShortTermRentalForm): ShortTermRental {
    return new ShortTermRental(form.description, form.commercial, form.largeSalesArea);
  }
}
