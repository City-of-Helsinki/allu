import {ApplicationExtension} from '../type/application-extension';
import {ApplicationType} from '../type/application-type';
import {TrafficArrangementImpedimentType} from '../traffic-arrangement-impediment-type';

export class TrafficArrangement extends ApplicationExtension {
  constructor(
    public pksCard?: boolean,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public workPurpose?: string,
    public terms?: string
  ) {
    super(ApplicationType[ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS], terms);
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType
      || TrafficArrangementImpedimentType[TrafficArrangementImpedimentType.NO_IMPEDIMENT];
  }
}
