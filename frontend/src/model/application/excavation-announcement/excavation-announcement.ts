import {ApplicationExtension} from '../type/application-extension';
import {ApplicationType} from '../type/application-type';
import {TimeUtil} from '../../../util/time.util';
import {TrafficArrangementImpedimentType} from '../traffic-arrangement-impediment-type';

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
    public additionalInfo?: string,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public terms?: string
  ) {
    super(ApplicationType[ApplicationType.EXCAVATION_ANNOUNCEMENT], terms);
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType
      || TrafficArrangementImpedimentType[TrafficArrangementImpedimentType.NO_IMPEDIMENT];
  }

  get uiWinterTimeOperation(): string {
    return TimeUtil.getUiDateString(this.winterTimeOperation);
  }

  set uiWinterTimeOperation(dateString: string) {
    this.winterTimeOperation = TimeUtil.getDateFromUi(dateString);
  }

  get uiWorkFinished(): string {
    return TimeUtil.getUiDateString(this.workFinished);
  }

  set uiWorkFinished(dateString: string) {
    this.workFinished = TimeUtil.getDateFromUi(dateString);
  }

  get uiUnauthorizedWorkStartTime(): string {
    return TimeUtil.getUiDateString(this.unauthorizedWorkStartTime);
  }

  set uiUnauthorizedWorkStartTime(dateString: string) {
    this.unauthorizedWorkStartTime = TimeUtil.getDateFromUi(dateString);
  }

  get uiUnauthorizedWorkEndTime(): string {
    return TimeUtil.getUiDateString(this.unauthorizedWorkEndTime);
  }

  set uiUnauthorizedWorkEndTime(dateString: string) {
    this.unauthorizedWorkEndTime = TimeUtil.getDateFromUi(dateString);
  }

  get uiGuaranteeEndTime(): string {
    return TimeUtil.getUiDateString(this.guaranteeEndTime);
  }

  set uiGuaranteeEndTime(dateString: string) {
    this.guaranteeEndTime = TimeUtil.getDateFromUi(dateString);
  }

  public get uiCustomerStartTime(): string {
    return TimeUtil.getUiDateString(this.customerStartTime);
  }

  public set uiCustomerStartTime(dateString: string) {
    this.customerStartTime = TimeUtil.getDateFromUi(dateString);
  }

  public get uiCustomerEndTime(): string {
    return TimeUtil.getUiDateString(this.customerEndTime);
  }

  public set uiCustomerEndTime(dateString: string) {
    this.customerEndTime = TimeUtil.getDateFromUi(dateString);
  }

  public get uiCustomerWinterTimeOperation(): string {
    return TimeUtil.getUiDateString(this.customerWinterTimeOperation);
  }

  public set uiCustomerWinterTimeOperation(dateString: string) {
    this.customerWinterTimeOperation = TimeUtil.getDateFromUi(dateString);
  }

  public get uiCustomerWorkFinished(): string {
    return TimeUtil.getUiDateString(this.customerWorkFinished);
  }

  public set uiCustomerWorkFinished(dateString: string) {
    this.customerWorkFinished = TimeUtil.getDateFromUi(dateString);
  }
}
