import {ApplicationExtension} from '../type/application-extension';
import {ApplicationType} from '../type/application-type';
import {TrafficArrangementImpedimentType} from '../traffic-arrangement-impediment-type';

export class ExcavationAnnouncement extends ApplicationExtension {
  constructor(
    public pksCard?: boolean,
    public constructionWork?: boolean,
    public maintenanceWork?: boolean,
    public emergencyWork?: boolean,
    public propertyConnectivity?: boolean,
    public selfSupervision?: boolean,
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
    public additionalInfo?: string,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public terms?: string,
    public operationalConditionReported?: Date,
    public workFinishedReported?: Date,
    public validityReported?: Date,
    public compactionAndBearingCapacityMeasurement?: boolean,
    public qualityAssuranceTest?: boolean,
    public cableReports: string[] = [],
    public placementContracts: string[] = [],
  ) {
    super(ApplicationType[ApplicationType.EXCAVATION_ANNOUNCEMENT], terms);
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType
      || TrafficArrangementImpedimentType[TrafficArrangementImpedimentType.NO_IMPEDIMENT];
  }
}
