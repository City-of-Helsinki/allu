import {DateReport} from '@model/application/date-report';
import {TimeUtil} from '@util/time.util';

export interface BackendApplicationDateReport {
  reportingDate: string;
  reportedDate: string;
  reportedEndDate: string;
}

export class ApplicationDateReportMapper {
  public static mapFrontend(dateReport: DateReport): BackendApplicationDateReport {
    return !!dateReport
      ? {
        reportingDate: TimeUtil.dateToBackend(dateReport.reportingDate),
        reportedDate: TimeUtil.dateToBackend(dateReport.reportedDate),
        reportedEndDate: TimeUtil.dateToBackend(dateReport.reportedEndDate)
      }
      : undefined;
  }
}
