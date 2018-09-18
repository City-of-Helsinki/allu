import {Action} from '@ngrx/store';
import {DateReport} from '@feature/application/date-reporting/date-report';

export enum ExcavationAnnouncementActionType {
  ReportOperationalCondition = '[ExcavationAnnouncement] Report operational condition date',
  ReportWorkFinished = '[ExcavationAnnouncement] Report work finished date',
  ReportCustomerDates = '[ExcavationAnnouncement] Report customer reported dates'
}

export class ReportOperationalCondition implements Action {
  readonly type = ExcavationAnnouncementActionType.ReportOperationalCondition;
  constructor(public payload: Date) {}
}

export class ReportWorkFinished implements Action {
  readonly type = ExcavationAnnouncementActionType.ReportWorkFinished;
  constructor(public payload: Date) {}
}

export class ReportCustomerDates implements Action {
  readonly type = ExcavationAnnouncementActionType.ReportCustomerDates;
  constructor(public payload: DateReport) {}
}

export type ExcavationAnnouncementActions =
  | ReportOperationalCondition
  | ReportWorkFinished
  | ReportCustomerDates;
