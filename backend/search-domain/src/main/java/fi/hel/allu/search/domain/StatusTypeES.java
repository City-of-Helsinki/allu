package fi.hel.allu.search.domain;

import fi.hel.allu.common.domain.types.StatusType;

/**
 * Elastic Search mapping for application type with ordinal number that's used to order search results.
 */
public class StatusTypeES {
  private StatusType status;

  public StatusTypeES() {
    // JSON deserialization
  }

  public StatusTypeES(StatusType status) {
    this.status = status;
  }

  public StatusType getValue() {
    return status;
  }

  public void setValue(StatusType status) {
    this.status = status;
  }

  public int getOrdinal() {
    return status.ordinal();
  }
}
