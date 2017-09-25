package fi.hel.allu.model.pricing;

import fi.hel.allu.common.exception.NotImplementedException;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.model.domain.InvoiceUnit;

import java.util.ArrayList;
import java.util.List;

public abstract class Pricing {

  private int priceInCents = 0;
  private List<InvoiceRow> invoiceRows = new ArrayList<>();

  public List<InvoiceRow> getInvoiceRows() {
    return invoiceRows;
  }

  protected void addInvoiceRow(InvoiceRowTag tag, InvoiceUnit unit, double quantity, int unitPrice, String explanation,
      int netPrice) {
    InvoiceRow row = new InvoiceRow();
    row.setTag(tag.toString());
    row.setUnit(unit);
    row.setQuantity(quantity);
    row.setUnitPrice(unitPrice);
    row.setRowText(explanation);
    row.setNetPrice(netPrice);
    invoiceRows.add(row);
  }

  public int getPriceInCents() {
    return priceInCents;
  }

  protected void setPriceInCents(int priceInCents) {
    this.priceInCents = priceInCents;
  }

  /**
   * Add a single location's price with given area and payment class
   *
   * @param locationKey the location's key (unique within application).
   * @param locationArea Location's area in square meters
   * @param paymentClass Payment class: 1,2, or 3.
   */
  public void addLocationPrice(int locationKey, double locationArea, int paymentClass) {
    throw new NotImplementedException("Location price with payment class not implemented in " + this.getClass());
  }

}
