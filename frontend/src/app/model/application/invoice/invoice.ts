import {InvoiceRow} from '@model/application/invoice/invoice-row';

export class Invoice {
  constructor(
    public id?: number,
    public applicationId?: number,
    public invoicableTime?: Date,
    public invoiced?: boolean,
    public sapIdPending?: boolean,
    public rows: InvoiceRow[] = []
  ) {}
}
