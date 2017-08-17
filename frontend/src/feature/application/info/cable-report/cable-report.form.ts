import {TimePeriod} from '../time-period';
import {CableReport} from '../../../../model/application/cable-report/cable-report';
import {CableInfoEntry} from '../../../../model/application/cable-report/cable-info-entry';
import {Application} from '../../../../model/application/application';
import {StringUtil} from '../../../../util/string.util';
import {ApplicationForm} from '../application-form';
import {TimeUtil} from '../../../../util/time.util';
import {ApplicationStatus} from '../../../../model/application/application-status';
import {OrdererIndex} from '../../../../model/application/cable-report/orderer-index';
import {CustomerRoleType} from '../../../../model/customer/customer-role-type';

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
    public cableInfo?: CableInfoForm,
    public specifiers?: Array<string>,
    public ordererIndex?: OrdererIndexForm
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
    cableReport.specifiers = specifiers;
    cableReport.ordererIndex = OrdererIndexForm.to(form.ordererIndex);
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
      CableInfoForm.from(cableReport),
      cableReport.specifiers,
      OrdererIndexForm.from(cableReport.ordererIndex)
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
    public selectedCableInfoTypes?: Array<string>,
    public mapExtractCount?: number,
    public cableInfoEntries?: Array<CableInfoEntry>
  ) {}

  static to(form: CableInfoForm, report: CableReport): CableReport {
    if (form) {
      report.mapExtractCount = form.mapExtractCount;
      report.infoEntries = form.cableInfoEntries.filter(entry => form.selectedCableInfoTypes.indexOf(entry.type) >= 0);
    }
    return report;
  }

  static from(cableReport: CableReport): CableInfoForm {
    let cableInfoForm = new CableInfoForm();
    cableInfoForm.mapExtractCount = cableReport.mapExtractCount;
    cableInfoForm.cableInfoEntries = cableReport.infoEntries;
    cableInfoForm.selectedCableInfoTypes = cableReport.infoEntries.map(entry => entry.type);
    return cableInfoForm;
  }
}

export class OrdererIndexForm {
  constructor(
    public customerRoleType: string,
    public index: number
  ) {}

  static to(form: OrdererIndexForm): OrdererIndex {
    return form ? new OrdererIndex(form.customerRoleType, form.index) : undefined;
  }

  static from(ordererIndex: OrdererIndex): OrdererIndexForm {
    return ordererIndex ? new OrdererIndexForm(ordererIndex.customerRoleType, ordererIndex.index) : OrdererIndexForm.createDefault();
  }

  static createDefault() {
    return new OrdererIndexForm(CustomerRoleType[CustomerRoleType.APPLICANT], 0);
  }
}
