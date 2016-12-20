import {ApplicationExtension} from '../type/application-extension';
import {CableInfoEntry} from './cable-info-entry';
import {Applicant} from '../applicant';
import {Contact} from '../contact';
import {ApplicationType} from '../type/application-type';

export class CableReport extends ApplicationExtension {
  public applicationType = ApplicationType[ApplicationType.CABLE_REPORT];

  constructor()
  constructor(
    cableSurveyRequired: boolean,
    cableReportId: string,
    workDescription: string,
    owner: Applicant,
    contact: Contact,
    mapExtractCount: number,
    infoEntries: Array<CableInfoEntry>
  )
  constructor(
    public cableSurveyRequired?: boolean,
    public cableReportId?: string,
    public workDescription?: string,
    public owner?: Applicant,
    public contact?: Contact,
    public mapExtractCount?: number,
    public infoEntries?: Array<CableInfoEntry>) {
    super();
    this.infoEntries = infoEntries || [];
  }

  get contactList(): Array<Contact> {
    return this.contact ? [this.contact] : undefined;
  }
}
