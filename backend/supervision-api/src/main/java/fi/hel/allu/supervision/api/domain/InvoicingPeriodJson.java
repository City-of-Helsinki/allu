package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Application invoicing period")
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

  @ApiModelProperty(value = "Id of the period")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Start time of the period")
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  @ApiModelProperty(value = "End time of the period")
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

}
