import {ApplicationExtension} from '../type/application-extension';
import {ApplicationType} from '../type/application-type';
import {TimeUtil} from '../../../util/time.util';
import {TrafficArrangementImpedimentType} from '../traffic-arrangement-impediment-type';

export class TrafficArrangement extends ApplicationExtension {
  constructor(
    public pksCard?: boolean,
    public workFinished?: Date,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public additionalInfo?: string,
    public terms?: string
  ) {
    super(ApplicationType[ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS], terms);
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType
      || TrafficArrangementImpedimentType[TrafficArrangementImpedimentType.NO_IMPEDIMENT];
  }

  get uiWorkFinished(): string {
    return TimeUtil.getUiDateString(this.workFinished);
  }

  set uiWorkFinished(dateString: string) {
    this.workFinished = TimeUtil.getDateFromUi(dateString);
  }
}
