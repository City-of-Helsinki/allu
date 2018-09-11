package fi.hel.allu.common.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

/**
 * Class to hold application dates reported by customer.
 *
 */
public class ApplicationDateReport {
  @NotNull
  private ZonedDateTime reportingDate;
  @NotNull
  private ZonedDateTime reportedDate;

  /**
   * Date when date was reported
   */
  public ZonedDateTime getReportingDate() {
    return reportingDate;
  }

  public void setReportingDate(ZonedDateTime reportingDate) {
    this.reportingDate = reportingDate;
  }

  /**
   * Reported date
   */
  public ZonedDateTime getReportedDate() {
    return reportedDate;
  }

  public void setReportedDate(ZonedDateTime reportedDate) {
    this.reportedDate = reportedDate;
  }

}
