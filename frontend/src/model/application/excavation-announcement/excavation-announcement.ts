import {ApplicationExtension} from '../type/application-extension';
import {ApplicationType} from '../type/application-type';
import {Applicant} from '../applicant/applicant';
import {Contact} from '../contact';
import {TimeUtil} from '../../../util/time.util';
import {TrafficArrangementImpedimentType} from '../traffic-arrangement-impediment-type';

export class ExcavationAnnouncement extends ApplicationExtension {
  constructor(
    public specifiers?: Array<string>,
    public contractor?: Applicant,
    public responsiblePerson?: Contact,
    public propertyDeveloper?: Applicant,
    public propertyDeveloperContact?: Contact,
    public pksCard?: boolean,
    public constructionWork?: boolean,
    public maintenanceWork?: boolean,
    public emergencyWork?: boolean,
    public propertyConnectivity?: boolean,
    public winterTimeOperation?: Date,
    public summerTimeOperation?: Date,
    public workFinished?: Date,
    public unauthorizedWorkStartTime?: Date,
    public unauthorizedWorkEndTime?: Date,
    public guaranteeEndTime?: Date,
    public cableReportId?: number,
    public additionalInfo?: string,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public terms?: string
  ) {
    super(ApplicationType[ApplicationType.EXCAVATION_ANNOUNCEMENT], specifiers, terms);
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType
      || TrafficArrangementImpedimentType[TrafficArrangementImpedimentType.NO_IMPEDIMENT];
  }

  get uiWinterTimeOperation(): string {
    return TimeUtil.getUiDateString(this.winterTimeOperation);
  }

  set uiWinterTimeOperation(dateString: string) {
    this.winterTimeOperation = TimeUtil.getDateFromUi(dateString);
  }

  get uiSummerTimeOperation(): string {
    return TimeUtil.getUiDateString(this.summerTimeOperation);
  }

  set uiSummerTimeOperation(dateString: string) {
    this.summerTimeOperation = TimeUtil.getDateFromUi(dateString);
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

  get responsiblePersonList(): Array<Contact> {
    return this.responsiblePerson ? [this.responsiblePerson] : undefined;
  }

  get propertyDeveloperContactList(): Array<Contact> {
    return this.propertyDeveloperContact ? [this.propertyDeveloperContact] : undefined;
  }
}
