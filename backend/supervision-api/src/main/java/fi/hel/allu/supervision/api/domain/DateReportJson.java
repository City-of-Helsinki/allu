package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Model to hold application date reported by customer")
public class DateReportJson {

  @NotNull
  private ZonedDateTime reportingDate;
  @NotNull
  private ZonedDateTime reportedDate;

  public DateReportJson() {
  }

  @Schema(description = "Date when customer reported application date")
  public ZonedDateTime getReportingDate() {
    return reportingDate;
  }

  public void setReportingDate(ZonedDateTime reportingDate) {
    this.reportingDate = reportingDate;
  }

  @Schema(description = "Reported date")
  public ZonedDateTime getReportedDate() {
    return reportedDate;
  }

  public void setReportedDate(ZonedDateTime reportedDate) {
    this.reportedDate = reportedDate;
  }
}
