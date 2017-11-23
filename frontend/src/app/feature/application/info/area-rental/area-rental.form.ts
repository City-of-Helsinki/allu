import {TimePeriod} from '../time-period';
import {Application} from '../../../../model/application/application';
import {ApplicationForm} from '../application-form';
import {AreaRental} from '../../../../model/application/area-rental/area-rental';

export class AreaRentalForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public pksCard?: boolean,
    public workFinished?: string,
    public calculatedPrice?: number,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public additionalInfo?: string
  ) {}

  static to(form: AreaRentalForm): AreaRental {
    const areaRental = new AreaRental();
    areaRental.pksCard = form.pksCard;
    areaRental.uiWorkFinished = form.workFinished;
    areaRental.trafficArrangements = form.trafficArrangements;
    areaRental.trafficArrangementImpedimentType = form.trafficArrangementImpedimentType;
    areaRental.additionalInfo = form.additionalInfo;
    return areaRental;
  }

  static from(application: Application, areaRental: AreaRental) {
    return new AreaRentalForm(
      new TimePeriod(application.startTime, application.endTime),
      areaRental.pksCard,
      areaRental.uiWorkFinished,
      application.calculatedPriceEuro,
      areaRental.trafficArrangements,
      areaRental.trafficArrangementImpedimentType,
      areaRental.additionalInfo
    );
  }
}
