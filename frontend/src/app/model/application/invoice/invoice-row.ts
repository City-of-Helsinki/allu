import {ChargeBasisUnit} from '@model/application/invoice/charge-basis-unit';

export class InvoiceRow {
  constructor(
    public unit?: ChargeBasisUnit,
    public quantity?: number,
    public text?: string,
    public explanation: string[] = [],
    public unitPrice?: number,
    public netPrice?: number
  ) {}
}
