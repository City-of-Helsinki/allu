import {ApplicationExtension} from '../type/application-extension';
import {ApplicationType} from '../type/application-type';
import {Contact} from '../../customer/contact';
import {TimeUtil} from '../../../util/time.util';
import {TrafficArrangementImpedimentType} from '../traffic-arrangement-impediment-type';
import {Customer} from '../../customer/customer';

export class AreaRental extends ApplicationExtension {
  constructor()
  constructor(
    contractor: Customer,
    responsiblePerson: Contact,
    workFinished: Date,
    trafficArrangements: string,
    trafficArrangementImpedimentType: string,
    additionalInfo: string,
    terms: string
  )
  constructor(
    public contractor?: Customer,
    public responsiblePerson?: Contact,
    public workFinished?: Date,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public additionalInfo?: string,
    public terms?: string
  ) {
    super(ApplicationType[ApplicationType.AREA_RENTAL], [], terms);
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType
      || TrafficArrangementImpedimentType[TrafficArrangementImpedimentType.NO_IMPEDIMENT];
  }

  get responsiblePersonList(): Array<Contact> {
    return this.responsiblePerson ? [this.responsiblePerson] : undefined;
  }

  get uiWorkFinished(): string {
    return TimeUtil.getUiDateString(this.workFinished);
  }

  set uiWorkFinished(dateString: string) {
    this.workFinished = TimeUtil.getDateFromUi(dateString);
  }
}
