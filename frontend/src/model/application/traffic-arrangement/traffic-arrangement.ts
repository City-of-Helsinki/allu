import {ApplicationExtension} from '../type/application-extension';
import {ApplicationType} from '../type/application-type';
import {Contact} from '../../customer/contact';
import {TimeUtil} from '../../../util/time.util';
import {TrafficArrangementImpedimentType} from '../traffic-arrangement-impediment-type';
import {Customer} from '../../customer/customer';

export class TrafficArrangement extends ApplicationExtension {
  constructor()
  constructor(
    specifiers: Array<string>,
    contractor: Customer,
    responsiblePerson: Contact,
    pksCard: boolean,
    workFinished: Date,
    trafficArrangements: string,
    trafficArrangementImpedimentType: string,
    additionalInfo: string,
    terms: string
  )
  constructor(
    public specifiers?: Array<string>,
    public contractor?: Customer,
    public responsiblePerson?: Contact,
    public pksCard?: boolean,
    public workFinished?: Date,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public additionalInfo?: string,
    public terms?: string
  ) {
    super(ApplicationType[ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS], specifiers, terms);
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType
      || TrafficArrangementImpedimentType[TrafficArrangementImpedimentType.NO_IMPEDIMENT];
  }

  get uiWorkFinished(): string {
    return TimeUtil.getUiDateString(this.workFinished);
  }

  set uiWorkFinished(dateString: string) {
    this.workFinished = TimeUtil.getDateFromUi(dateString);
  }

  get responsiblePersonList(): Array<Contact> {
    return this.responsiblePerson ? [this.responsiblePerson] : undefined;
  }
}
