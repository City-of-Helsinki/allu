import {TimePeriod} from '../time-period';
import {ApplicantForm} from '../applicant/applicant.form';
import {Contact} from '../../../../model/application/contact';
import {Some} from '../../../../util/option';
import {Application} from '../../../../model/application/application';
import {TrafficArrangement} from '../../../../model/application/traffic-arrangement/traffic-arrangement';
import {ApplicationForm} from '../application-form';

export class TrafficArrangementForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public applicant?: ApplicantForm,
    public contacts?: Array<Contact>,
    public contractor?: ApplicantForm,
    public responsiblePerson?: Array<Contact>,
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
    arrangement.contractor = Some(form.contractor).map(contractor => ApplicantForm.toApplicant(contractor)).orElse(undefined);
    arrangement.responsiblePerson = Some(form.responsiblePerson).filter(persons => persons.length > 0).map(c => c[0]).orElse(undefined);
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
      undefined, // these are added by subcomponents (application and contact)
      undefined,
      undefined,
      undefined,
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
