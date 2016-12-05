import {ApplicationExtension} from '../type/application-extension';
import {CableInfoEntry} from './cable-info-entry';
import {Applicant} from '../applicant';
import {Contact} from '../contact';
import {ApplicationType} from '../type/application-type';
import {ApplicationSpecifier} from '../type/application-specifier';

export class CableReport extends ApplicationExtension {
  public applicationType = ApplicationType[ApplicationType.CABLE_REPORT];

  constructor()
  constructor(
    cableReportId: string,
    workDescription: string,
    owner: Applicant,
    contact: Contact,
    mapExtractCount: number,
    infoEntries: Array<CableInfoEntry>
  )
  constructor(
    public cableReportId?: string,
    public workDescription?: string,
    public owner?: Applicant,
    public contact?: Contact,
    public mapExtractCount?: number,
    public infoEntries?: Array<CableInfoEntry>) {
    super();
    this.infoEntries = infoEntries || [];
  }
}
