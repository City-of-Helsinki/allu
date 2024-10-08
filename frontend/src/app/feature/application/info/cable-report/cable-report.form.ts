import {TimePeriod} from '@feature/application/info/time-period';
import {CableReport} from '@model/application/cable-report/cable-report';
import {CableInfoEntry} from '@model/application/cable-report/cable-info-entry';
import {Application} from '@model/application/application';
import {ApplicationForm} from '@feature/application/info/application-form';
import {TimeUtil} from '@util/time.util';
import {ApplicationStatus} from '@model/application/application-status';
import {OrdererId} from '@model/application/cable-report/orderer-id';
import {CustomerRoleType} from '@model/customer/customer-role-type';

export interface OrdererIdForm {
  id?: number;
  customerRoleType?: string;
  index?: number;
}

export function toOrdererId(form: OrdererIdForm): OrdererId {
  return form ? OrdererId.of(form.id, form.customerRoleType, form.index) : undefined;
}

export function fromOrdererId(ordererId: OrdererId): OrdererIdForm {
  if (ordererId) {
    return {
      id: ordererId.id,
      customerRoleType: ordererId.customerRoleType,
      index: ordererId.index
    };
  } else {
    return createDefaultOrdererId();
  }
}

export function createDefaultOrdererId(): OrdererIdForm {
  return {
    id: undefined,
    customerRoleType: CustomerRoleType[CustomerRoleType.APPLICANT],
    index: 0
  };
}

export interface CableReportForm extends ApplicationForm {
   validityTime?: Date;
   constructionWork?: boolean;
   maintenanceWork?: boolean;
   emergencyWork?: boolean;
   propertyConnectivity?: boolean;
   reportTimes?: TimePeriod;
   workDescription?: string;
   ordererId?: OrdererIdForm;
   selectedCableInfoTypes?: string[];
   mapExtractCount?: number;
   cableInfoEntries?: CableInfoEntry[];
}

export function to(form: CableReportForm, validityTime: Date): CableReport {
  const cableReport = new CableReport();
  cableReport.validityTime = validityTime;
  cableReport.constructionWork = form.constructionWork;
  cableReport.maintenanceWork = form.maintenanceWork;
  cableReport.emergencyWork = form.emergencyWork;
  cableReport.propertyConnectivity = form.propertyConnectivity;
  cableReport.workDescription = form.workDescription;
  cableReport.ordererId = toOrdererId(form.ordererId);
  cableReport.mapExtractCount = form.mapExtractCount;
  cableReport.infoEntries = getInfoEntriesForTypes(form.cableInfoEntries, form.selectedCableInfoTypes);
  return cableReport;
}

function getInfoEntriesForTypes(entries: CableInfoEntry[], types: string[]): CableInfoEntry[] {
  return entries
    ? entries.filter(entry => types.indexOf(entry.type) >= 0)
    : [];
}

export function from(application: Application): CableReportForm {
  const cableReport = <CableReport>application.extension || new CableReport();
  return {
    name: application.name || 'Johtoselvitys', // Cable reports have no name so set default
    validityTime: getValidityTime(ApplicationStatus[application.status], cableReport),
    constructionWork: cableReport.constructionWork,
    maintenanceWork: cableReport.maintenanceWork,
    emergencyWork: cableReport.emergencyWork,
    propertyConnectivity: cableReport.propertyConnectivity,
    reportTimes: new TimePeriod(application.startTime, application.endTime),
    workDescription: cableReport.workDescription,
    ordererId: cableReport.ordererId,
    mapExtractCount: cableReport.mapExtractCount,
    cableInfoEntries: cableReport.infoEntries,
    selectedCableInfoTypes: getSelectedInfoTypes(cableReport.infoEntries)
  };
}

function getSelectedInfoTypes(entries: CableInfoEntry[]): string[] {
  return entries ? entries.map(entry => entry.type) : undefined;
}

function getValidityTime(status: ApplicationStatus, cableReport: CableReport): Date {

  if (status === ApplicationStatus.DECISION || 
      status === ApplicationStatus.REPLACED ||
      status ===  ApplicationStatus.FINISHED ||
      status ===  ApplicationStatus.ARCHIVED) {
    return cableReport.validityTime;
  } else {
    return
  }
}
