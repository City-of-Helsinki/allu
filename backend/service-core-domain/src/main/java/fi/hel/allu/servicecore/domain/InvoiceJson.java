package fi.hel.allu.servicecore.domain;

import javax.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;

public class InvoiceJson {
  private Integer id;
  private Integer applicationId;
  private ZonedDateTime invoicableTime;
  private boolean invoiced;
  private boolean sapIdPending;
  @NotNull
  private List<InvoiceRowJson> rows;

  public InvoiceJson(Integer id, Integer applicationId, ZonedDateTime invoicableTime, boolean invoiced,
      boolean sapIdPending,
      List<InvoiceRowJson> rows) {
    this.id = id;
    this.applicationId = applicationId;
    this.invoicableTime = invoicableTime;
    this.invoiced = invoiced;
    this.sapIdPending = sapIdPending;
    this.rows = rows;
  }

  public InvoiceJson() {
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
   * Is this invoice waiting for invoicee's SAP ID?
   *
   * @return true if invoice can't be sent because invoicee's SAP ID is not
   *         known.
   */
  public boolean isSapIdPending() {
    return sapIdPending;
  }

  public void setSapIdPending(boolean sapIdPending) {
    this.sapIdPending = sapIdPending;
  }

  /**
   * Get the invoice rows for this invoice.
   *
   * @return List of invoice rows
   */
  public List<InvoiceRowJson> getRows() {
    return rows;
  }

  public void setRows(List<InvoiceRowJson> rows) {
    this.rows = rows;
  }

}
