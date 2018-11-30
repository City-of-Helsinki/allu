import {Action} from '@ngrx/store';
import {DateReport} from '@model/application/date-report';

export enum DateReportingActionType {
  ReportOperationalCondition = '[DateReporting] Report operational condition date',
  ReportCustomerOperationalCondition = '[DateReporting] Report customers operational condition date',
  ReportWorkFinished = '[DateReporting] Report work finished date',
  ReportCustomerWorkFinished = '[DateReporting] Report customers work finished date',
  ReportCustomerValidity = '[DateReporting] Report customers validity dates',
  ReportLocationCustomerValidity = '[DateReporting] Report customers validity dates for location',
}

export class ReportOperationalCondition implements Action {
  readonly type = DateReportingActionType.ReportOperationalCondition;
  constructor(public payload: Date) {}
}

export class ReportCustomerOperationalCondition implements Action {
  readonly type = DateReportingActionType.ReportCustomerOperationalCondition;
  constructor(public payload: DateReport) {}
}

export class ReportWorkFinished implements Action {
  readonly type = DateReportingActionType.ReportWorkFinished;
  constructor(public payload: Date) {}
}

export class ReportCustomerWorkFinished implements Action {
  readonly type = DateReportingActionType.ReportCustomerWorkFinished;
  constructor(public payload: DateReport) {}
}

export class ReportCustomerValidity implements Action {
  readonly type = DateReportingActionType.ReportCustomerValidity;
  constructor(public payload: DateReport) {}
}

export class ReportLocationCustomerValidity implements Action {
  readonly type = DateReportingActionType.ReportLocationCustomerValidity;
  readonly payload: {id: number, report: DateReport};
  constructor(id: number, report: DateReport) {
    this.payload = {id, report};
  }
}

export type DateReportingActions =
  | ReportOperationalCondition
  | ReportCustomerOperationalCondition
  | ReportWorkFinished
  | ReportCustomerWorkFinished
  | ReportCustomerValidity
  | ReportLocationCustomerValidity;

