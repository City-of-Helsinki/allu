import {TimePeriod} from '../time-period';
import {ShortTermRental} from '../../../../model/application/short-term-rental/short-term-rental';
import {Application} from '../../../../model/application/application';
import {ApplicationForm} from '../application-form';
import {CustomerWithContactsForm} from '../../../customerregistry/customer/customer-with-contacts.form';

export class ShortTermRentalForm implements ApplicationForm {
  constructor(
    public name?: string,
    public description?: string,
    public rentalTimes?: TimePeriod,
    public commercial?: boolean,
    public largeSalesArea?: boolean,
    public calculatedPrice?: number,
    public applicant?: CustomerWithContactsForm) {}

  static from(application: Application, rental: ShortTermRental): ShortTermRentalForm {
    return new ShortTermRentalForm(
      application.name,
      rental.description,
      new TimePeriod(application.startTime, application.endTime),
      rental.commercial,
      rental.largeSalesArea,
      application.calculatedPriceEuro
    );
  }

  static to(form: ShortTermRentalForm): ShortTermRental {
    const rental =  new ShortTermRental();
    rental.description = form.description;
    rental.commercial = form.commercial;
    rental.largeSalesArea = form.largeSalesArea;
    return rental;
  }
}
