import {ApplicationExtension} from '../type/application-extension';
import {ApplicationType} from '../type/application-type';
import {TrafficArrangementImpedimentType} from '../traffic-arrangement-impediment-type';
import {TimeUtil} from '../../../util/time.util';

export class ExcavationAnnouncement extends ApplicationExtension {
  constructor(
    public pksCard?: boolean,
    public constructionWork?: boolean,
    public maintenanceWork?: boolean,
    public emergencyWork?: boolean,
    public propertyConnectivity?: boolean,
    public winterTimeOperation?: Date,
    public workFinished?: Date,
    public unauthorizedWorkStartTime?: Date,
    public unauthorizedWorkEndTime?: Date,
    public guaranteeEndTime?: Date,
    public customerStartTime?: Date,
    public customerEndTime?: Date,
    public customerWinterTimeOperation?: Date,
    public customerWorkFinished?: Date,
    public cableReportId?: number,
    public workPurpose?: string,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public terms?: string,
    public operationalConditionReported?: Date,
    public workFinishedReported?: Date
  ) {
    super(ApplicationType[ApplicationType.EXCAVATION_ANNOUNCEMENT], terms);
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType
      || TrafficArrangementImpedimentType[TrafficArrangementImpedimentType.NO_IMPEDIMENT];
  }
}
