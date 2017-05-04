import {BackendInvoiceRow} from '../backend-model/backend-invoice-row';
import {InvoiceRow} from '../../model/application/invoice/invoice-row';
import {InvoiceUnit} from '../../model/application/invoice/invoice-unit';
import {Some} from '../../util/option';
export class InvoiceRowMapper {

  public static mapBackendArray(rows: Array<BackendInvoiceRow> = []): Array<InvoiceRow> {
    return rows.map(this.mapBackend);
  }

  public static mapFrontendArray(rows: Array<InvoiceRow>): Array<BackendInvoiceRow> {
    return rows.map(this.mapFrontEnd);
  }

  public static mapBackend(backendInvoiceRow: BackendInvoiceRow): InvoiceRow {
    return new InvoiceRow(
      Some(backendInvoiceRow.unit).map(unit => InvoiceUnit[unit]).orElse(InvoiceUnit.PIECE),
      backendInvoiceRow.quantity,
      backendInvoiceRow.rowText,
      backendInvoiceRow.unitPrice,
      backendInvoiceRow.netPrice,
      backendInvoiceRow.manuallySet
    );
  }

  public static mapFrontEnd(invoiceRow: InvoiceRow): BackendInvoiceRow {
    return {
      unit: Some(invoiceRow.unit).map(unit => InvoiceUnit[unit]).orElse(InvoiceUnit[InvoiceUnit.PIECE]),
      quantity: invoiceRow.quantity,
      rowText: invoiceRow.rowText,
      unitPrice: invoiceRow.unitPrice,
      netPrice: invoiceRow.netPrice,
      manuallySet: invoiceRow.manuallySet
    };
  }
}
