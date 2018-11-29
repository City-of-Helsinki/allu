package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;

public class CustomerLocationValidity {

  private Integer id;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private ZonedDateTime reportingTime;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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

  public ZonedDateTime getReportingTime() {
    return reportingTime;
  }

  public void setReportingTime(ZonedDateTime reportingTime) {
    this.reportingTime = reportingTime;
  }
}
