import {TimePeriod} from '../time-period';
import {ExcavationAnnouncement} from '../../../../model/application/excavation-announcement/excavation-announcement';
import {Application} from '../../../../model/application/application';
import {ApplicationForm} from '../application-form';

export class ExcavationAnnouncementForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public pksCard?: boolean,
    public constructionWork?: boolean,
    public maintenanceWork?: boolean,
    public emergencyWork?: boolean,
    public propertyConnectivity?: boolean,
    public winterTimeOperation?: string,
    public summerTimeOperation?: string,
    public workFinished?: string,
    public unauthorizedWork?: TimePeriod,
    public guaranteeEndTime?: string,
    public calculatedPrice?: number,
    public priceOverride?: number,
    public priceOverrideReason?: string,
    public cableReportId?: number,
    public additionalInfo?: string,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public specifiers?: Array<string>) {}

  static to(form: ExcavationAnnouncementForm, specifiers: Array<string>): ExcavationAnnouncement {
    let ea = new ExcavationAnnouncement();
    ea.pksCard = form.pksCard;
    ea.constructionWork = form.constructionWork;
    ea.maintenanceWork = form.maintenanceWork;
    ea.emergencyWork = form.emergencyWork;
    ea.propertyConnectivity = form.propertyConnectivity;
    ea.uiWinterTimeOperation = form.winterTimeOperation;
    ea.uiSummerTimeOperation = form.summerTimeOperation;
    ea.uiWorkFinished = form.workFinished;
    ea.unauthorizedWorkStartTime = form.unauthorizedWork.startTime;
    ea.unauthorizedWorkEndTime = form.unauthorizedWork.endTime;
    ea.uiGuaranteeEndTime = form.guaranteeEndTime;
    ea.cableReportId = form.cableReportId;
    ea.additionalInfo = form.additionalInfo;
    ea.trafficArrangements = form.trafficArrangements;
    ea.trafficArrangementImpedimentType = form.trafficArrangementImpedimentType;
    ea.specifiers = specifiers;
    return ea;
  }

  static from(application: Application, excavation: ExcavationAnnouncement) {
    return new ExcavationAnnouncementForm(
      new TimePeriod(application.startTime, application.endTime),
      excavation.pksCard,
      excavation.constructionWork,
      excavation.maintenanceWork,
      excavation.emergencyWork,
      excavation.propertyConnectivity,
      excavation.uiWinterTimeOperation,
      excavation.uiSummerTimeOperation,
      excavation.uiWorkFinished,
      new TimePeriod(excavation.unauthorizedWorkStartTime, excavation.unauthorizedWorkEndTime),
      excavation.uiGuaranteeEndTime,
      application.calculatedPriceEuro,
      application.priceOverrideEuro,
      application.priceOverrideReason,
      excavation.cableReportId,
      excavation.additionalInfo,
      excavation.trafficArrangements,
      excavation.trafficArrangementImpedimentType,
      excavation.specifiers
    );
  }
}
