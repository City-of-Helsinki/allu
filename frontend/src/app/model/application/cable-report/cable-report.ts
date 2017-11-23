import {ApplicationExtension} from '../type/application-extension';
import {CableInfoEntry} from './cable-info-entry';
import {ApplicationType} from '../type/application-type';
import {TimeUtil} from '../../../util/time.util';
import {OrdererId} from './orderer-id';

export class CableReport extends ApplicationExtension {
  constructor(
    public validityTime?: Date,
    public cableSurveyRequired?: boolean,
    public mapUpdated?: boolean,
    public constructionWork?: boolean,
    public maintenanceWork?: boolean,
    public emergencyWork?: boolean,
    public propertyConnectivity?: boolean,
    public cableReportId?: string,
    public workDescription?: string,
    public mapExtractCount?: number,
    public infoEntries?: Array<CableInfoEntry>,
    public ordererId?: OrdererId) {
    super(ApplicationType[ApplicationType.CABLE_REPORT]);
    this.infoEntries = infoEntries || [];
  }

  get uiValidityTime(): string {
    return TimeUtil.getUiDateString(this.validityTime);
  }
}
