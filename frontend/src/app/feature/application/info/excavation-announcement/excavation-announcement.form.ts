import {TimePeriod} from '../time-period';
import {ExcavationAnnouncement} from '../../../../model/application/excavation-announcement/excavation-announcement';
import {Application} from '../../../../model/application/application';
import {ApplicationForm} from '../application-form';
import {NumberUtil} from '../../../../util/number.util';
import { TimeUtil } from '@app/util/time.util';

export class ExcavationAnnouncementForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public pksCard?: boolean,
    public constructionWork?: boolean,
    public maintenanceWork?: boolean,
    public emergencyWork?: boolean,
    public propertyConnectivity?: boolean,
    public winterTimeOperation?: Date,
    public workFinished?: string,
    public unauthorizedWork?: TimePeriod,
    public guaranteeEndTime?: string,
    public customerValidityTimes?: TimePeriod,
    public customerWinterTimeOperation?: Date,
    public customerWorkFinished?: Date,
    public calculatedPrice?: number,
    public cableReportId?: number,
    public workPurpose?: string,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string,
    public TERMS?: string) {}

  static to(form: ExcavationAnnouncementForm, original: ExcavationAnnouncement = new ExcavationAnnouncement()): ExcavationAnnouncement {
    const ea = {...original};
    ea.pksCard = form.pksCard;
    ea.constructionWork = form.constructionWork;
    ea.maintenanceWork = form.maintenanceWork;
    ea.emergencyWork = form.emergencyWork;
    ea.propertyConnectivity = form.propertyConnectivity;
    ea.winterTimeOperation = form.winterTimeOperation;
    ea.workFinished = TimeUtil.getDateFromUi(form.workFinished);
    ea.unauthorizedWorkStartTime = form.unauthorizedWork.startTime;
    ea.unauthorizedWorkEndTime = form.unauthorizedWork.endTime;
    ea.guaranteeEndTime = TimeUtil.getDateFromUi(form.guaranteeEndTime);
    ea.customerStartTime = form.customerValidityTimes.startTime;
    ea.customerEndTime = form.customerValidityTimes.endTime;
    ea.customerWinterTimeOperation = form.customerWinterTimeOperation;
    ea.customerWorkFinished = form.customerWorkFinished;
    ea.cableReportId = form.cableReportId;
    ea.workPurpose = form.workPurpose;
    ea.trafficArrangements = form.trafficArrangements;
    ea.trafficArrangementImpedimentType = form.trafficArrangementImpedimentType;
    ea.terms = form.TERMS;
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
      TimeUtil.getUiDateString(excavation.workFinished),
      new TimePeriod(excavation.unauthorizedWorkStartTime, excavation.unauthorizedWorkEndTime),
      TimeUtil.getUiDateString(excavation.guaranteeEndTime),
      new TimePeriod(excavation.customerStartTime, excavation.customerEndTime),
      excavation.customerWinterTimeOperation,
      excavation.customerWorkFinished,
      NumberUtil.toEuros(application.calculatedPrice),
      excavation.cableReportId,
      excavation.workPurpose,
      excavation.trafficArrangements,
      excavation.trafficArrangementImpedimentType,
      excavation.terms
    );
  }
}
