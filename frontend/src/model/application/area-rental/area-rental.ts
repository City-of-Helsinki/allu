import {ApplicationExtension} from '../type/application-extension';
import {ApplicationType} from '../type/application-type';
import {Applicant} from '../applicant';
import {Contact} from '../contact';

export class AreaRental extends ApplicationExtension {
  constructor()
  constructor(
    contractor: Applicant,
    responsiblePerson: Contact,
    trafficArrangements: string,
    additionalInfo: string,
    terms: string
  )
  constructor(
    public contractor?: Applicant,
    public responsiblePerson?: Contact,
    public trafficArrangements?: string,
    public additionalInfo?: string,
    public terms?: string
  ) {
    super(ApplicationType[ApplicationType.AREA_RENTAL], [], terms);
  }

  get responsiblePersonList(): Array<Contact> {
    return this.responsiblePerson ? [this.responsiblePerson] : undefined;
  }
}
