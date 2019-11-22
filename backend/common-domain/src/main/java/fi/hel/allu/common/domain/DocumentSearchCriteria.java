package fi.hel.allu.common.domain;

import java.time.ZonedDateTime;

import fi.hel.allu.common.domain.types.ApplicationType;

/**
 * Search criteria for document (decision, operational condition and work
 * finished documents)
 *
 */
public class DocumentSearchCriteria {

  private ZonedDateTime after;
  private ZonedDateTime before;
  private ApplicationType applicationType;

  public ZonedDateTime getAfter() {
    return after;
  }

  public void setAfter(ZonedDateTime after) {
    this.after = after;
  }

  public ZonedDateTime getBefore() {
    return before;
  }

  public void setBefore(ZonedDateTime before) {
    this.before = before;
  }

  public ApplicationType getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(ApplicationType applicationType) {
    this.applicationType = applicationType;
  }
}
