package fi.hel.allu.model.pricing;

import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.domain.InvoiceUnit;

import java.util.ArrayList;
import java.util.List;

public abstract class Pricing {

  private List<InvoiceRow> invoiceRows = new ArrayList<>();

  public List<InvoiceRow> getInvoiceRows() {
    return invoiceRows;
  }

  protected void addInvoiceRow(InvoiceUnit unit, double quantity, int unitPrice, String explanation, int netPrice) {
    InvoiceRow row = new InvoiceRow();
    row.setUnit(unit);
    row.setQuantity(quantity);
    row.setUnitPrice(unitPrice);
    row.setRowText(explanation);
    row.setNetPrice(netPrice);
    invoiceRows.add(row);
  }

}
