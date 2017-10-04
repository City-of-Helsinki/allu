package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Single invoice for application
 */
public class Invoice {
  private Integer id;
  private Integer applicationId;
  private ZonedDateTime invoicableTime;
  private boolean invoiced;
  private List<InvoiceRow> rows;

  public Invoice(Integer id, Integer applicationId, ZonedDateTime invoicableTime, boolean invoiced,
      List<InvoiceRow> rows) {
    this.id = id;
    this.applicationId = applicationId;
    this.invoicableTime = invoicableTime;
    this.invoiced = invoiced;
    setRows(rows);
  }

  public Invoice() {
    // for deserialization
  }

  /**
   * Get the database ID for the invoice
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Get the database ID of the application this invoice belongs to.
   *
   * @return Application's database ID
   */
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * Get the time after which this invoice is invoicable
   */
  public ZonedDateTime getInvoicableTime() {
    return invoicableTime;
  }

  public void setInvoicableTime(ZonedDateTime invoicableTime) {
    this.invoicableTime = invoicableTime;
  }

  /**
   * Has this invoice been sent to invoicing?
   *
   * @return true if invoice has been invoiced
   */
  public boolean isInvoiced() {
    return invoiced;
  }

  public void setInvoiced(boolean invoiced) {
    this.invoiced = invoiced;
  }

  /**
   * Get the invoice rows for this invoice.
   *
   * @return List of invoice rows
   */
  public List<InvoiceRow> getRows() {
    return rows;
  }

  public void setRows(List<InvoiceRow> rows) {
    if (rows != null) {
      this.rows = rows;
    } else {
      this.rows = new ArrayList<>();
    }
  }

}
