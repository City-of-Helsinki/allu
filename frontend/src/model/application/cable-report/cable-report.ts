import {ApplicationExtension} from '../type/application-extension';
import {CableInfoEntry} from './cable-info-entry';
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
    public mapExtractCount?: number,
    public infoEntries?: Array<CableInfoEntry>) {
    super(ApplicationType[ApplicationType.CABLE_REPORT], specifiers);
    this.infoEntries = infoEntries || [];
  }

  get uiValidityTime(): string {
    return TimeUtil.getUiDateString(this.validityTime);
  }
}
