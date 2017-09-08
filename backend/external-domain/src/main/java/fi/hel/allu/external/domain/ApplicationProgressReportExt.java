package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

/**
 * Used to report application progress. Null-value fields are allowed (only
 * non-null fields are processed).
 */
public class ApplicationProgressReportExt {
  private ZonedDateTime workFinished;
  private ZonedDateTime winterTimeOperation;

  public ZonedDateTime getWorkFinished() {
    return workFinished;
  }

  public void setWorkFinished(ZonedDateTime workFinished) {
    this.workFinished = workFinished;
  }

  public ZonedDateTime getWinterTimeOperation() {
    return winterTimeOperation;
  }

  public void setWinterTimeOperation(ZonedDateTime winterTimeOperation) {
    this.winterTimeOperation = winterTimeOperation;
  }
}
