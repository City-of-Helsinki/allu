import {TimePeriod} from '../time-period';
import {CustomerForm} from '../../../customerregistry/customer/customer.form';
import {Some} from '../../../../util/option';
import {Application} from '../../../../model/application/application';
import {TrafficArrangement} from '../../../../model/application/traffic-arrangement/traffic-arrangement';
import {ApplicationForm} from '../application-form';
import {CustomerWithContactsForm} from '../../../customerregistry/customer/customer-with-contacts.form';
import {ArrayUtil} from '../../../../util/array-util';

export class TrafficArrangementForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public pksCard?: boolean,
    public workFinished?: Date,
    public calculatedPrice?: number,
    public priceOverride?: number,
    public priceOverrideReason?: string,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public additionalInfo?: string,
    public specifiers?: Array<string>
  ) {}

  static to(form: TrafficArrangementForm): TrafficArrangement {
    let arrangement = new TrafficArrangement();
    arrangement.pksCard = form.pksCard;
    arrangement.workFinished = form.workFinished;
    arrangement.trafficArrangements = form.trafficArrangements;
    arrangement.trafficArrangementImpedimentType = form.trafficArrangementImpedimentType;
    arrangement.additionalInfo = form.additionalInfo;
    arrangement.specifiers = form.specifiers;
    return arrangement;
  }

  static from(application: Application, arrangement: TrafficArrangement) {
    return new TrafficArrangementForm(
      new TimePeriod(application.startTime, application.endTime),
      arrangement.pksCard,
      arrangement.workFinished,
      application.calculatedPriceEuro,
      application.priceOverrideEuro,
      application.priceOverrideReason,
      arrangement.trafficArrangements,
      arrangement.trafficArrangementImpedimentType,
      arrangement.additionalInfo,
      arrangement.specifiers
    );
  }
}
