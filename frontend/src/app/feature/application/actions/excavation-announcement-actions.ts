import {Action} from '@ngrx/store';
import {RequiredTasks} from '@model/application/required-tasks';
import {ApplicationDateReport} from '@model/application/application-date-report';

export enum ExcavationAnnouncementActionType {
  ReportOperationalCondition = '[ExcavationAnnouncement] Report operational condition date',
  ReportCustomerOperationalCondition = '[ExcavationAnnouncement] Report customers operational condition date',
  ReportCustomerValidity = '[ExcavationAnnouncement] Report customers validity dates',
  SetRequiredTasks = '[ExcavationAnnouncement] Set required tasks'
}

export class ReportOperationalCondition implements Action {
  readonly type = ExcavationAnnouncementActionType.ReportOperationalCondition;
  constructor(public payload: Date) {}
}

export class ReportCustomerOperationalCondition implements Action {
  readonly type = ExcavationAnnouncementActionType.ReportCustomerOperationalCondition;
  constructor(public payload: ApplicationDateReport) {}
}



export class ReportCustomerValidity implements Action {
  readonly type = ExcavationAnnouncementActionType.ReportCustomerValidity;
  constructor(public payload: ApplicationDateReport) {}
}

export class SetRequiredTasks implements Action {
  readonly type = ExcavationAnnouncementActionType.SetRequiredTasks;
  constructor(public payload: RequiredTasks) {}
}

export type ExcavationAnnouncementActions =
  | ReportOperationalCondition
  | ReportCustomerOperationalCondition
  | ReportCustomerValidity
  | SetRequiredTasks;
