package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;


public class InvoicingPeriod {
  private Integer id;

  private Integer applicationId;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private boolean invoiced;

  public InvoicingPeriod() {
  }

  public InvoicingPeriod(int applicationId, ZonedDateTime startTime, ZonedDateTime endTime) {
    this.applicationId = applicationId;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  public boolean isInvoiced() {
    return invoiced;
  }

  public void setInvoiced(boolean invoiced) {
    this.invoiced = invoiced;
  }

}
