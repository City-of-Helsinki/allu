import {ApplicationDateReport} from '@model/application/application-date-report';
import {Action} from '@ngrx/store';

export enum ExcavationAnnouncementActionType {
  ReportOperationalCondition = '[ExcavationAnnouncement] Report operational condition date',
  ReportWorkFinished = '[ExcavationAnnouncement] Report work finished date',
  ReportCustomerOperationalCondition = '[ExcavationAnnouncement] Customer reported operational condition date',
  ReportCustomerWorkFinished = '[ExcavationAnnouncement] Customer reported work finished date',
}

export class ReportOperationalCondition implements Action {
  readonly type = ExcavationAnnouncementActionType.ReportOperationalCondition;
  constructor(public payload: ApplicationDateReport) {}
}

export class ReportWorkFinished implements Action {
  readonly type = ExcavationAnnouncementActionType.ReportWorkFinished;
  constructor(public payload: ApplicationDateReport) {}
}

export class ReportCustomerOperationalCondition implements Action {
  readonly type = ExcavationAnnouncementActionType.ReportCustomerOperationalCondition;
  constructor(public payload: ApplicationDateReport) {}
}

export class ReportCustomerWorkFinished implements Action {
  readonly type = ExcavationAnnouncementActionType.ReportCustomerWorkFinished;
  constructor(public payload: ApplicationDateReport) {}
}

export type ExcavationAnnouncementActions =
  | ReportOperationalCondition
  | ReportWorkFinished
  | ReportCustomerOperationalCondition
  | ReportCustomerWorkFinished;
