import {TimePeriod} from '../time-period';
import {Application} from '../../../../model/application/application';
import {TrafficArrangement} from '../../../../model/application/traffic-arrangement/traffic-arrangement';
import {ApplicationForm} from '../application-form';
import {NumberUtil} from '../../../../util/number.util';

export class TrafficArrangementForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public calculatedPrice?: number,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public workPurpose?: string,
    public terms?: string
  ) {}

  static to(form: TrafficArrangementForm): TrafficArrangement {
    const arrangement = new TrafficArrangement();
    arrangement.trafficArrangements = form.trafficArrangements;
    arrangement.trafficArrangementImpedimentType = form.trafficArrangementImpedimentType;
    arrangement.workPurpose = form.workPurpose;
    arrangement.terms = form.terms;
    return arrangement;
  }

  static from(application: Application, arrangement: TrafficArrangement) {
    return new TrafficArrangementForm(
      new TimePeriod(application.startTime, application.endTime),
      NumberUtil.toEuros(application.calculatedPrice),
      arrangement.trafficArrangements,
      arrangement.trafficArrangementImpedimentType,
      arrangement.workPurpose,
      arrangement.terms
    );
  }
}
