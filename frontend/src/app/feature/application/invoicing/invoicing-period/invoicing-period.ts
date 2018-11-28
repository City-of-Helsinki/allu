export class InvoicingPeriod {
  constructor(
    public id: number,
    public applicationId: number,
    public startTime: Date,
    public endTime: Date,
    public invoiced: boolean
  ) {}
}
