import {TimePeriod} from '../time-period';
import {ExcavationAnnouncement} from '@model/application/excavation-announcement/excavation-announcement';
import {Application} from '@model/application/application';
import {ApplicationForm} from '@feature/application/info/application-form';
import {TimeUtil} from '@app/util/time.util';

export interface ExcavationAnnouncementForm extends ApplicationForm {
  validityTimes?: TimePeriod;
  pksCard?: boolean;
  constructionWork?: boolean;
  maintenanceWork?: boolean;
  emergencyWork?: boolean;
  propertyConnectivity?: boolean;
  winterTimeOperation?: Date;
  workFinished?: string;
  unauthorizedWork?: TimePeriod;
  guaranteeEndTime?: string;
  customerValidityTimes?: TimePeriod;
  customerWinterTimeOperation?: Date;
  customerWorkFinished?: Date;
  cableReportId?: number;
  workPurpose?: string;
  trafficArrangements?: string;
  trafficArrangementImpedimentType?: string;
  compactionAndBearingCapacityMeasurement?: boolean;
  qualityAssuranceTest?: boolean;
  terms?: string;
}

export function to(form: ExcavationAnnouncementForm, original: ExcavationAnnouncement = new ExcavationAnnouncement()):
  ExcavationAnnouncement {
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
  ea.compactionAndBearingCapacityMeasurement = form.compactionAndBearingCapacityMeasurement;
  ea.qualityAssuranceTest = form.qualityAssuranceTest;
  ea.terms = form.terms;
  return ea;
}

export function from(application: Application, excavation: ExcavationAnnouncement) {
  return {
    name: application.name || 'Kaivuilmoitus', // Cable reports have no name so set default
    validityTimes: new TimePeriod(application.startTime, application.endTime),
    pksCard: excavation.pksCard,
    constructionWork: excavation.constructionWork,
    maintenanceWork: excavation.maintenanceWork,
    emergencyWork: excavation.emergencyWork,
    propertyConnectivity: excavation.propertyConnectivity,
    winterTimeOperation: excavation.winterTimeOperation,
    workFinished: TimeUtil.getUiDateString(excavation.workFinished),
    unauthorizedWork: new TimePeriod(excavation.unauthorizedWorkStartTime, excavation.unauthorizedWorkEndTime),
    guaranteeEndTime: TimeUtil.getUiDateString(excavation.guaranteeEndTime),
    customerValidityTimes: new TimePeriod(excavation.customerStartTime, excavation.customerEndTime),
    customerWinterTimeOperation: excavation.customerWinterTimeOperation,
    customerWorkFinished: excavation.customerWorkFinished,
    cableReportId: excavation.cableReportId,
    workPurpose: excavation.workPurpose,
    trafficArrangements: excavation.trafficArrangements,
    trafficArrangementImpedimentType: excavation.trafficArrangementImpedimentType,
    compactionAndBearingCapacityMeasurement: excavation.compactionAndBearingCapacityMeasurement,
    qualityAssuranceTest: excavation.qualityAssuranceTest,
    terms: excavation.terms
  };
}
