import {TimePeriod} from '../time-period';
import {ApplicantForm} from '../applicant/applicant.form';
import {Contact} from '../../../../model/application/contact';
import {CableReport} from '../../../../model/application/cable-report/cable-report';
import {Some} from '../../../../util/option';
import {CableInfoEntry} from '../../../../model/application/cable-report/cable-info-entry';
import {ApplicationExtension} from '../../../../model/application/type/application-extension';
import {Application} from '../../../../model/application/application';
import {StringUtil} from '../../../../util/string.util';

export class CableReportForm {
  constructor(
    public cableSurveyRequired?: boolean,
    public reportTimes?: TimePeriod,
    public workDescription?: string,
    public company?: ApplicantForm,
    public orderer?: Array<Contact>,
    public owner?: ApplicantForm,
    public contact?: Array<Contact>,
    public cableInfo?: CableInfoForm,
    public specifiers?: Array<string>
  ) {}

  static to(form: CableReportForm, specifiers: Array<string>): CableReport {
    let cableReport = new CableReport();
    cableReport.cableSurveyRequired = form.cableSurveyRequired;
    cableReport.workDescription = form.workDescription;
    cableReport.owner = Some(form.owner).filter(owner => !!owner.name).map(owner => ApplicantForm.toApplicant(owner)).orElse(undefined);
    cableReport.contact = Some(form.contact).filter(c => c.length > 0).map(c => c[0]).orElse(undefined);
    cableReport.specifiers = specifiers;
    return CableInfoForm.to(form.cableInfo, cableReport);
  }

  static from(application: Application): CableReportForm {
    let cableReport = <CableReport>application.extension || new CableReport();
    return new CableReportForm(
      cableReport.cableSurveyRequired,
      new TimePeriod(application.uiStartTime, application.uiEndTime),
      cableReport.workDescription,
      undefined, // these are added by subcomponents (application and contact)
      undefined,
      undefined,
      undefined,
      new CableInfoForm(cableReport.mapExtractCount, cableReport.infoEntries),
      cableReport.specifiers
    );
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
