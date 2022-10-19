package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Application invoicing period")
public class InvoicingPeriodJson {

  private Integer id;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;

  public InvoicingPeriodJson() {
  }

  public InvoicingPeriodJson(Integer id, ZonedDateTime startTime, ZonedDateTime endTime) {
    this.id = id;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  @Schema(description = "Id of the period")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Start time of the period")
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  @Schema(description = "End time of the period")
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

}
