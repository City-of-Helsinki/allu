import {ChargeBasisUnit} from '@model/application/invoice/charge-basis-unit';
import {Invoice} from '@model/application/invoice/invoice';
import {InvoiceRow} from '@model/application/invoice/invoice-row';
import {TimeUtil} from '@util/time.util';

export interface BackendInvoiceRow {
  unit: ChargeBasisUnit;
  quantity: number;
  text: string;
  unitPrice: number;
  netPrice: number;
}

export interface BackendInvoice {
  id: number;
  applicationId: number;
  invoicableTime: string;
  invoiced: boolean;
  sapIdPending: boolean;
  rows: BackendInvoiceRow[];
}

export class InvoiceMapper {
  static mapBackendInvoices(invoices: BackendInvoice[]): Invoice[] {
    return invoices ? invoices.map(invoice => this.mapBackend(invoice)) : [];
  }

  static mapBackend(invoice: BackendInvoice): Invoice {
    return new Invoice(
      invoice.id,
      invoice.applicationId,
      TimeUtil.dateFromBackend(invoice.invoicableTime),
      invoice.invoiced,
      invoice.sapIdPending,
      this.mapBackendRows(invoice.rows)
    );
  }

  static mapBackendRows(invoiceRows: BackendInvoiceRow[]): InvoiceRow[] {
    return invoiceRows ? invoiceRows.map(row => this.mapBackendRow(row)) : [];
  }

  static mapBackendRow(invoiceRow: BackendInvoiceRow): InvoiceRow {
    return new InvoiceRow(
      invoiceRow.unit,
      invoiceRow.quantity,
      invoiceRow.text,
      invoiceRow.unitPrice,
      invoiceRow.netPrice
    );
  }
}
