import {ApplicationExtension} from '../type/application-extension';
import {CableInfoEntry} from './cable-info-entry';
import {Applicant} from '../applicant/applicant';
import {Contact} from '../contact';
import {ApplicationType} from '../type/application-type';
import {TimeUtil} from '../../../util/time.util';

export class CableReport extends ApplicationExtension {
  constructor(
    public specifiers?: Array<string>,
    public validityTime?: Date,
    public cableSurveyRequired?: boolean,
    public mapUpdated?: boolean,
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

  get uiValidityTime(): string {
    return TimeUtil.getUiDateString(this.validityTime);
  }
}
