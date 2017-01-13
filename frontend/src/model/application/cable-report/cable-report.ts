import {ApplicationExtension} from '../type/application-extension';
import {CableInfoEntry} from './cable-info-entry';
import {Applicant} from '../applicant';
import {Contact} from '../contact';
import {ApplicationType} from '../type/application-type';

export class CableReport extends ApplicationExtension {
  constructor()
  constructor(
    specifiers: Array<string>,
    cableSurveyRequired: boolean,
    pksCard: boolean,
    constructionWork: boolean,
    maintenanceWork: boolean,
    emergencyWork: boolean,
    propertyConnectivity: boolean,
    cableReportId: string,
    workDescription: string,
    owner: Applicant,
    contact: Contact,
    mapExtractCount: number,
    infoEntries: Array<CableInfoEntry>
  )
  constructor(
    public specifiers?: Array<string>,
    public cableSurveyRequired?: boolean,
    public pksCard?: boolean,
    public constructionWork?: boolean,
    public maintenanceWork?: boolean,
    public emergencyWork?: boolean,
    public propertyConnectivity?: boolean,
    public cableReportId?: string,
    public workDescription?: string,
    public owner?: Applicant,
    public contact?: Contact,
    public mapExtractCount?: number,
    public infoEntries?: Array<CableInfoEntry>) {
    super(ApplicationType[ApplicationType.CABLE_REPORT], specifiers);
    this.infoEntries = infoEntries || [];
  }

  get contactList(): Array<Contact> {
    return this.contact ? [this.contact] : undefined;
  }
}
