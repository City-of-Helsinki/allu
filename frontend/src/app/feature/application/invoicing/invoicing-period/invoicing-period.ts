import {ApplicationStatus} from '@app/model/application/application-status';

export class InvoicingPeriod {
  constructor(
    public id: number,
    public applicationId: number,
    public startTime: Date,
    public endTime: Date,
    public invoiced: boolean,
    public invoicableStatus: ApplicationStatus
  ) {}
}
