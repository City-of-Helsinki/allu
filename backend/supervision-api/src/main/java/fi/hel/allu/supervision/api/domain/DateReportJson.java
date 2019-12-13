package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Model to hold application date reported by customer")
public class DateReportJson {

  @NotNull
  private ZonedDateTime reportingDate;
  @NotNull
  private ZonedDateTime reportedDate;

  public DateReportJson() {
  }

  @ApiModelProperty(value = "Date when customer reported application date")
  public ZonedDateTime getReportingDate() {
    return reportingDate;
  }

  public void setReportingDate(ZonedDateTime reportingDate) {
    this.reportingDate = reportingDate;
  }

  @ApiModelProperty(value = "Reported date")
  public ZonedDateTime getReportedDate() {
    return reportedDate;
  }

  public void setReportedDate(ZonedDateTime reportedDate) {
    this.reportedDate = reportedDate;
  }
}
