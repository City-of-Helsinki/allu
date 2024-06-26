import {ApplicationExtension, WorkFinishedDates} from '../type/application-extension';
import {ApplicationType} from '../type/application-type';
import {TrafficArrangementImpedimentType} from '../traffic-arrangement-impediment-type';

export class AreaRental extends ApplicationExtension implements WorkFinishedDates {
  constructor(
    public pksCard?: boolean,
    public majorDisturbance?: boolean,
    public workFinished?: Date,
    public customerWorkFinished?: Date,
    public workFinishedReported?: Date,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public workPurpose?: string,
    public additionalInfo?: string,
    public terms?: string
  ) {
    super(ApplicationType[ApplicationType.AREA_RENTAL], terms);
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType
      || TrafficArrangementImpedimentType[TrafficArrangementImpedimentType.NO_IMPEDIMENT];
  }
}

export function isAreaRental(extension: ApplicationExtension): extension is AreaRental {
  return ApplicationType.AREA_RENTAL === extension.applicationType;
}
