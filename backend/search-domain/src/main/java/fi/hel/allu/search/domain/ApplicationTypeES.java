package fi.hel.allu.search.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import fi.hel.allu.common.domain.types.ApplicationType;

/**
 * Elastic Search mapping for application type with ordinal number that's used to order search results.
 */
public class ApplicationTypeES {
  private ApplicationType applicationType;

  public ApplicationTypeES() {
    // JSON deserialization
  }

  public ApplicationTypeES(ApplicationType applicationType) {
    this.applicationType = applicationType;
  }

  public ApplicationType getValue() {
    return applicationType;
  }

  public void setValue(ApplicationType applicationType) {
    this.applicationType = applicationType;
  }

  @JsonProperty(access = Access.READ_ONLY)
  public int getOrdinal() {
    return this.applicationType.ordinal();
  }
}
