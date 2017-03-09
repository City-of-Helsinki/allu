import {ApplicationExtension} from '../type/application-extension';
import {ApplicationType} from '../type/application-type';
import {Applicant} from '../applicant';
import {Contact} from '../contact';
import {TimeUtil} from '../../../util/time.util';

export class AreaRental extends ApplicationExtension {
  constructor()
  constructor(
    contractor: Applicant,
    responsiblePerson: Contact,
    workFinished: Date,
    trafficArrangements: string,
    additionalInfo: string,
    terms: string
  )
  constructor(
    public contractor?: Applicant,
    public responsiblePerson?: Contact,
    public workFinished?: Date,
    public trafficArrangements?: string,
    public additionalInfo?: string,
    public terms?: string
  ) {
    super(ApplicationType[ApplicationType.AREA_RENTAL], [], terms);
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
