import {TimePeriod} from '../time-period';
import {ApplicantForm} from '../applicant/applicant.form';
import {Contact} from '../../../../model/application/contact';
import {CableReport} from '../../../../model/application/cable-report/cable-report';
import {Some} from '../../../../util/option';
import {CableInfoEntry} from '../../../../model/application/cable-report/cable-info-entry';

export class CableReportForm {
  constructor(
    public cableSurveyRequired?: boolean,
    public reportTimes?: TimePeriod,
    public workDescription?: string,
    public company?: ApplicantForm,
    public orderer?: Array<Contact>,
    public owner?: ApplicantForm,
    public contact?: Array<Contact>,
    public cableInfo?: CableInfoForm
  ) {}

  static to(form: CableReportForm, eventType: string): CableReport {
    let cableReport = new CableReport();
    cableReport.type = eventType;
    cableReport.workDescription = form.workDescription;
    cableReport.owner = Some(form.owner).map(owner => ApplicantForm.fromApplicant(owner));
    cableReport.contact = Some(form.contact).filter(c => c.length > 0).map(c => c[0]).orElse(undefined);

    Some(form.cableInfo).do(info => {
      cableReport.mapExtractCount = info.mapExtractCount;
      cableReport.infoEntries = info.cableInfoEntries;
    });
    return cableReport;
  }
}

export class CableInfoForm {
  constructor(
    public mapExtractCount?: number,
    public cableInfoEntries?: Array<CableInfoEntry>
  ) {}
}
