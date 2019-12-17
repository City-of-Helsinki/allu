package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Invoice")
public class InvoiceJson {
  private Integer id;
  private Integer applicationId;
  private ZonedDateTime invoicableTime;
  private ZonedDateTime sentTime;
  private boolean invoiced;
  private boolean sapIdPending;
  @NotNull
  private List<InvoiceRowJson> rows;

  public InvoiceJson(Integer id, Integer applicationId, ZonedDateTime invoicableTime, boolean invoiced,
      boolean sapIdPending, ZonedDateTime sentTime, List<InvoiceRowJson> rows) {
    this.id = id;
    this.applicationId = applicationId;
    this.invoicableTime = invoicableTime;
    this.invoiced = invoiced;
    this.sapIdPending = sapIdPending;
    this.rows = rows;
    this.sentTime = sentTime;
  }

  public InvoiceJson() {
    // for deserialization
  }

  @ApiModelProperty(value = "ID of the invoice")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "ID of the application this invoice belongs to")
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  @ApiModelProperty(value = "Time after which this invoice is invoicable")
  public ZonedDateTime getInvoicableTime() {
    return invoicableTime;
  }

  public void setInvoicableTime(ZonedDateTime invoicableTime) {
    this.invoicableTime = invoicableTime;
  }

  @ApiModelProperty(value = "Value indicating whether this invoice has been sent to invoicing")
  public boolean isInvoiced() {
    return invoiced;
  }

  public void setInvoiced(boolean invoiced) {
    this.invoiced = invoiced;
  }

  @ApiModelProperty(value = "Is this invoice waiting for invoicee's SAP ID")
  public boolean isSapIdPending() {
    return sapIdPending;
  }

  public void setSapIdPending(boolean sapIdPending) {
    this.sapIdPending = sapIdPending;
  }

  @ApiModelProperty(value = "Rows of the invoice")
  public List<InvoiceRowJson> getRows() {
    return rows;
  }

  public void setRows(List<InvoiceRowJson> rows) {
    this.rows = rows;
  }

  @ApiModelProperty(value = "Time when invoice was sent to invoicing")
  public ZonedDateTime getSentTime() {
    return sentTime;
  }

  public void setSentTime(ZonedDateTime sentTime) {
    this.sentTime = sentTime;
  }

}
