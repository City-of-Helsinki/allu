import {Action} from '@ngrx/store';
import {ApplicationDateReport} from '@model/application/application-date-report';

export enum DateReportingActionType {
  ReportOperationalCondition = '[DateReporting] Report operational condition date',
  ReportCustomerOperationalCondition = '[DateReporting] Report customers operational condition date',
  ReportWorkFinished = '[DateReporting] Report work finished date',
  ReportCustomerWorkFinished = '[DateReporting] Report customers work finished date',
  ReportCustomerValidity = '[DateReporting] Report customers validity dates',
}

export class ReportOperationalCondition implements Action {
  readonly type = DateReportingActionType.ReportOperationalCondition;
  constructor(public payload: Date) {}
}

export class ReportCustomerOperationalCondition implements Action {
  readonly type = DateReportingActionType.ReportCustomerOperationalCondition;
  constructor(public payload: ApplicationDateReport) {}
}

export class ReportWorkFinished implements Action {
  readonly type = DateReportingActionType.ReportWorkFinished;
  constructor(public payload: Date) {}
}

export class ReportCustomerWorkFinished implements Action {
  readonly type = DateReportingActionType.ReportCustomerWorkFinished;
  constructor(public payload: ApplicationDateReport) {}
}

export class ReportCustomerValidity implements Action {
  readonly type = DateReportingActionType.ReportCustomerValidity;
  constructor(public payload: ApplicationDateReport) {}
}

export type DateReportingActions =
  | ReportOperationalCondition
  | ReportCustomerOperationalCondition
  | ReportWorkFinished
  | ReportCustomerWorkFinished
  | ReportCustomerValidity;

