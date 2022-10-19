package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.validator.NotFalse;
import io.swagger.v3.oas.annotations.media.Schema;

@NotFalse(rules = {"startDate, startDateNotAfterEndDate, {validityperiod.start}"})
@Schema(description = "Model to hold date period reported by customer")
public class DatePeriodReportJson {

  @NotNull
  private ZonedDateTime reportingDate;
  private ZonedDateTime reportedStartDate;
  private ZonedDateTime reportedEndDate;

  public DatePeriodReportJson() {
  }

  @Schema(description = "Date when customer reported period")
  public ZonedDateTime getReportingDate() {
    return reportingDate;
  }

  public void setReportingDate(ZonedDateTime reportingDate) {
    this.reportingDate = reportingDate;
  }

  @Schema(description = "Reported start date")
  public ZonedDateTime getReportedStartDate() {
    return reportedStartDate;
  }

  public void setReportedStartDate(ZonedDateTime reportedStartDate) {
    this.reportedStartDate = reportedStartDate;
  }

  @Schema(description = "Reported end date")
  public ZonedDateTime getReportedEndDate() {
    return reportedEndDate;
  }

  public void setReportedEndDate(ZonedDateTime reportedEndDate) {
    this.reportedEndDate = reportedEndDate;
  }

  @JsonIgnore
  public boolean getStartDateNotAfterEndDate() {
    return reportedStartDate == null || reportedEndDate == null || !reportedStartDate.isAfter(reportedEndDate);
  }

}
