import {TimePeriod} from '../time-period';
import {CustomerForm} from '../../../customerregistry/customer/customer.form';
import {Contact} from '../../../../model/customer/contact';
import {Some} from '../../../../util/option';
import {Application} from '../../../../model/application/application';
import {ApplicationForm} from '../application-form';
import {AreaRental} from '../../../../model/application/area-rental/area-rental';
import {CustomerWithContactsForm} from '../../../customerregistry/customer/customer-with-contacts.form';
import {ArrayUtil} from '../../../../util/array-util';

export class AreaRentalForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public applicant?: CustomerWithContactsForm,
    public contractor?: CustomerWithContactsForm,
    public workFinished?: string,
    public calculatedPrice?: number,
    public priceOverride?: number,
    public priceOverrideReason?: string,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public additionalInfo?: string
  ) {}

  static to(form: AreaRentalForm): AreaRental {
    let areaRental = new AreaRental();
    Some(form.contractor).do(c => {
      areaRental.contractor = CustomerForm.toCustomer(c.customer);
      areaRental.responsiblePerson = ArrayUtil.first(c.contacts);
    });

    areaRental.uiWorkFinished = form.workFinished;
    areaRental.trafficArrangements = form.trafficArrangements;
    areaRental.trafficArrangementImpedimentType = form.trafficArrangementImpedimentType;
    areaRental.additionalInfo = form.additionalInfo;
    return areaRental;
  }

  static from(application: Application, areaRental: AreaRental) {
    return new AreaRentalForm(
      new TimePeriod(application.startTime, application.endTime),
      undefined, // these are added by subcomponents (application and contact)
      undefined,
      areaRental.uiWorkFinished,
      application.calculatedPriceEuro,
      application.priceOverrideEuro,
      application.priceOverrideReason,
      areaRental.trafficArrangements,
      areaRental.trafficArrangementImpedimentType,
      areaRental.additionalInfo
    );
  }
}
