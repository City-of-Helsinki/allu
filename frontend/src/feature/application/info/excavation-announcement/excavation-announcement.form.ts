import {TimePeriod} from '../time-period';
import {ApplicantForm} from '../applicant/applicant.form';
import {Contact} from '../../../../model/application/contact';
import {Some} from '../../../../util/option';
import {ExcavationAnnouncement} from '../../../../model/application/excavation-announcement/excavation-announcement';
import {Application} from '../../../../model/application/application';
import {ApplicationForm} from '../application-form';

export class ExcavationAnnouncementForm implements ApplicationForm {
  constructor(
    public validityTimes?: TimePeriod,
    public applicant?: ApplicantForm,
    public contacts?: Array<Contact>,
    public contractor?: ApplicantForm,
    public responsiblePerson?: Array<Contact>,
    public propertyDeveloper?: ApplicantForm,
    public propertyDeveloperContact?: Array<Contact>,
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
    public cableReportId?: number,
    public additionalInfo?: string,
    public trafficArrangements?: string,
    public trafficArrangementImpedimentType?: string) {}

  static to(form: ExcavationAnnouncementForm, specifiers: Array<string>): ExcavationAnnouncement {
    let ea = new ExcavationAnnouncement();
    ea.contractor = Some(form.contractor).map(contractor => ApplicantForm.toApplicant(contractor)).orElse(undefined);
    ea.responsiblePerson = Some(form.responsiblePerson).filter(persons => persons.length > 0).map(c => c[0]).orElse(undefined);
    ea.propertyDeveloper = Some(form.propertyDeveloper).map(developer => ApplicantForm.toApplicant(developer)).orElse(undefined);
    ea.propertyDeveloperContact = Some(form.propertyDeveloperContact)
      .filter(contacts => contacts.length > 0)
      .map(c => c[0]).orElse(undefined);
    ea.pksCard = form.pksCard;
    ea.constructionWork = form.constructionWork;
    ea.maintenanceWork = form.maintenanceWork;
    ea.emergencyWork = form.emergencyWork;
    ea.propertyConnectivity = form.propertyConnectivity;
    ea.uiWinterTimeOperation = form.winterTimeOperation;
    ea.uiSummerTimeOperation = form.summerTimeOperation;
    ea.uiWorkFinished = form.workFinished;
    ea.uiUnauthorizedWorkStartTime = form.unauthorizedWork.startTime;
    ea.uiUnauthorizedWorkEndTime = form.unauthorizedWork.endTime;
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
      new TimePeriod(application.uiStartTime, application.uiEndTime),
      undefined, // these are added by subcomponents (application and contact)
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      excavation.pksCard,
      excavation.constructionWork,
      excavation.maintenanceWork,
      excavation.emergencyWork,
      excavation.propertyConnectivity,
      excavation.uiWinterTimeOperation,
      excavation.uiSummerTimeOperation,
      excavation.uiWorkFinished,
      new TimePeriod(excavation.uiUnauthorizedWorkStartTime, excavation.uiUnauthorizedWorkEndTime),
      excavation.uiGuaranteeEndTime,
      excavation.cableReportId,
      excavation.additionalInfo,
      excavation.trafficArrangements,
      excavation.trafficArrangementImpedimentType
    );
  }
}
