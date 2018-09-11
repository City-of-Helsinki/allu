import {ApplicationDateReport} from '@model/application/application-date-report';
import {TimeUtil} from '@util/time.util';

export interface BackendApplicationDateReport {
  reportingDate: string;
  reportedDate: string;
}

export class ApplicationDateReportMapper {
  public static mapFrontend(dateReport: ApplicationDateReport): BackendApplicationDateReport {
    return !!dateReport
      ? {
        reportingDate: TimeUtil.dateToBackend(dateReport.reportingDate),
        reportedDate: TimeUtil.dateToBackend(dateReport.reportedDate)
      }
      : undefined;
  }
}
