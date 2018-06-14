import {TimePeriod} from '../time-period';
import {ExcavationAnnouncement} from '../../../../model/application/excavation-announcement/excavation-announcement';
import {Application} from '../../../../model/application/application';
import {ApplicationForm} from '../application-form';
import {NumberUtil} from '../../../../util/number.util';

export class ExcavationAnnouncementForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public pksCard?: boolean,
    public constructionWork?: boolean,
    public maintenanceWork?: boolean,
    public emergencyWork?: boolean,
    public propertyConnectivity?: boolean,
    public winterTimeOperation?: Date,
    public workFinished?: Date,
    public unauthorizedWork?: TimePeriod,
    public guaranteeEndTime?: string,
    public customerValidityTimes?: TimePeriod,
    public customerWinterTimeOperation?: Date,
    public customerWorkFinished?: Date,
    public calculatedPrice?: number,
    public cableReportId?: number,
    public additionalInfo?: string,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public terms?: string) {}

  static to(form: ExcavationAnnouncementForm): ExcavationAnnouncement {
    const ea = new ExcavationAnnouncement();
    ea.pksCard = form.pksCard;
    ea.constructionWork = form.constructionWork;
    ea.maintenanceWork = form.maintenanceWork;
    ea.emergencyWork = form.emergencyWork;
    ea.propertyConnectivity = form.propertyConnectivity;
    ea.winterTimeOperation = form.winterTimeOperation;
    ea.workFinished = form.workFinished;
    ea.unauthorizedWorkStartTime = form.unauthorizedWork.startTime;
    ea.unauthorizedWorkEndTime = form.unauthorizedWork.endTime;
    ea.uiGuaranteeEndTime = form.guaranteeEndTime;
    ea.customerStartTime = form.customerValidityTimes.startTime;
    ea.customerEndTime = form.customerValidityTimes.endTime;
    ea.customerWinterTimeOperation = form.customerWinterTimeOperation;
    ea.customerWorkFinished = form.customerWorkFinished;
    ea.cableReportId = form.cableReportId;
    ea.additionalInfo = form.additionalInfo;
    ea.trafficArrangements = form.trafficArrangements;
    ea.trafficArrangementImpedimentType = form.trafficArrangementImpedimentType;
    ea.terms = form.terms;
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
      excavation.winterTimeOperation,
      excavation.workFinished,
      new TimePeriod(excavation.unauthorizedWorkStartTime, excavation.unauthorizedWorkEndTime),
      excavation.uiGuaranteeEndTime,
      new TimePeriod(excavation.customerStartTime, excavation.customerEndTime),
      excavation.customerWinterTimeOperation,
      excavation.customerWorkFinished,
      NumberUtil.toEuros(application.calculatedPrice),
      excavation.cableReportId,
      excavation.additionalInfo,
      excavation.trafficArrangements,
      excavation.trafficArrangementImpedimentType,
      excavation.terms
    );
  }
}
