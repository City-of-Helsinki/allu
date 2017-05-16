import {TimePeriod} from '../time-period';
import {ApplicantForm} from '../applicant/applicant.form';
import {Contact} from '../../../../model/application/contact';
import {CableReport} from '../../../../model/application/cable-report/cable-report';
import {Some} from '../../../../util/option';
import {CableInfoEntry} from '../../../../model/application/cable-report/cable-info-entry';
import {Application} from '../../../../model/application/application';
import {StringUtil} from '../../../../util/string.util';
import {ApplicationForm} from '../application-form';
import {TimeUtil} from '../../../../util/time.util';
import {ApplicationStatus} from '../../../../model/application/application-status';

export class CableReportForm implements ApplicationForm {
  constructor(
    public validityTime?: Date,
    public cableSurveyRequired?: boolean,
    public mapUpdated?: boolean,
    public constructionWork?: boolean,
    public maintenanceWork?: boolean,
    public emergencyWork?: boolean,
    public propertyConnectivity?: boolean,
    public reportTimes?: TimePeriod,
    public workDescription?: string,
    public orderer?: ApplicantForm,
    public ordererContacts?: Array<Contact>,
    public owner?: ApplicantForm,
    public ownerContact?: Array<Contact>,
    public cableInfo?: CableInfoForm,
    public specifiers?: Array<string>
  ) {}

  static to(form: CableReportForm, validityTime: Date, specifiers: Array<string>): CableReport {
    let cableReport = new CableReport();
    cableReport.validityTime = validityTime;
    cableReport.cableSurveyRequired = form.cableSurveyRequired;
    cableReport.mapUpdated = form.mapUpdated;
    cableReport.constructionWork = form.constructionWork;
    cableReport.maintenanceWork = form.maintenanceWork;
    cableReport.emergencyWork = form.emergencyWork;
    cableReport.propertyConnectivity = form.propertyConnectivity;
    cableReport.workDescription = form.workDescription;
    cableReport.owner = Some(form.owner).filter(owner => !!owner.name).map(owner => ApplicantForm.toApplicant(owner)).orElse(undefined);
    cableReport.contact = Some(form.ownerContact).filter(c => c.length > 0).map(c => c[0]).orElse(undefined);
    cableReport.specifiers = specifiers;
    return CableInfoForm.to(form.cableInfo, cableReport);
  }

  static from(application: Application): CableReportForm {
    let cableReport = <CableReport>application.extension || new CableReport();
    return new CableReportForm(
      this.validityTime(ApplicationStatus[application.status], cableReport),
      cableReport.cableSurveyRequired,
      cableReport.mapUpdated,
      cableReport.constructionWork,
      cableReport.maintenanceWork,
      cableReport.emergencyWork,
      cableReport.propertyConnectivity,
      new TimePeriod(application.startTime, application.endTime),
      cableReport.workDescription,
      undefined, // these are added by subcomponents (application and contact)
      undefined,
      undefined,
      undefined,
      new CableInfoForm(cableReport.mapExtractCount, cableReport.infoEntries),
      cableReport.specifiers
    );
  }

  private static validityTime(status: ApplicationStatus, cableReport: CableReport): Date {
    if (status >= ApplicationStatus.DECISION) {
      return cableReport.validityTime;
    } else {
      return TimeUtil.add(new Date(), 1, 'months');
    }
  }
}

export class CableInfoForm {
  constructor(
    public mapExtractCount?: number,
    public cableInfoEntries?: Array<CableInfoEntry>
  ) {}

  static to(form: CableInfoForm, report: CableReport): CableReport {
    if (form) {
      report.mapExtractCount = form.mapExtractCount;
      report.infoEntries = form.cableInfoEntries.filter(entry => !StringUtil.isEmpty(entry.additionalInfo));
    }
    return report;
  }


}
