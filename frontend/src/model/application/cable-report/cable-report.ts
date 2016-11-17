import {ApplicationCategoryType} from '../../../feature/application/type/application-category';
import {ApplicationTypeData} from '../type/application-type-data';
import {CableInfoEntry} from './cable-info-entry';
import {Applicant} from '../applicant';
import {Contact} from '../contact';

export class CableReport extends ApplicationTypeData {
  public applicationCategory = ApplicationCategoryType[ApplicationCategoryType.CABLE_REPORT];

  constructor()
  constructor(
    type: string,
    cableReportId: string,
    workDescription: string,
    owner: Applicant,
    contact: Contact,
    mapExtractCount: number,
    infoEntries: Array<CableInfoEntry>
  )
  constructor(
    public type?: string,
    public cableReportId?: string,
    public workDescription?: string,
    public owner?: Applicant,
    public contact?: Contact,
    public mapExtractCount?: number,
    public infoEntries?: Array<CableInfoEntry>
    ) {
    super();
    this.infoEntries = infoEntries || [];
  }
}
