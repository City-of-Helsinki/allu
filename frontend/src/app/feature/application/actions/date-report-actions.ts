import {Action} from '@ngrx/store';
import {ApplicationDateReport} from '@model/application/application-date-report';

export enum DateReportActionType {
  ReportWorkFinished = '[DateReport] Report work finished date',
  ReportCustomerWorkFinished = '[DateReport] Report customers work finished date',
}

export class ReportWorkFinished implements Action {
  readonly type = DateReportActionType.ReportWorkFinished;
  constructor(public payload: Date) {}
}

export class ReportCustomerWorkFinished implements Action {
  readonly type = DateReportActionType.ReportCustomerWorkFinished;
  constructor(public payload: ApplicationDateReport) {}
}

export type DateReportActions =
  | ReportWorkFinished
  | ReportCustomerWorkFinished;

