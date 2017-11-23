import {TimePeriod} from '../time-period';
import {Application} from '../../../../model/application/application';
import {TrafficArrangement} from '../../../../model/application/traffic-arrangement/traffic-arrangement';
import {ApplicationForm} from '../application-form';

export class TrafficArrangementForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public pksCard?: boolean,
    public workFinished?: Date,
    public calculatedPrice?: number,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public additionalInfo?: string
  ) {}

  static to(form: TrafficArrangementForm): TrafficArrangement {
    const arrangement = new TrafficArrangement();
    arrangement.pksCard = form.pksCard;
    arrangement.workFinished = form.workFinished;
    arrangement.trafficArrangements = form.trafficArrangements;
    arrangement.trafficArrangementImpedimentType = form.trafficArrangementImpedimentType;
    arrangement.additionalInfo = form.additionalInfo;
    return arrangement;
  }

  static from(application: Application, arrangement: TrafficArrangement) {
    return new TrafficArrangementForm(
      new TimePeriod(application.startTime, application.endTime),
      arrangement.pksCard,
      arrangement.workFinished,
      application.calculatedPriceEuro,
      arrangement.trafficArrangements,
      arrangement.trafficArrangementImpedimentType,
      arrangement.additionalInfo
    );
  }
}
