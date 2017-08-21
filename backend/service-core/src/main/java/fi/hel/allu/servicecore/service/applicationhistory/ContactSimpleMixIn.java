package fi.hel.allu.servicecore.service.applicationhistory;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Ignored fields when serializing Contact for history comparisons
 */
public abstract class ContactSimpleMixIn {
  @JsonIgnore public abstract Integer getCustomerId();
  @JsonIgnore public abstract String getStreetAddress();
  @JsonIgnore public abstract String getPostalCode();
  @JsonIgnore public abstract String getCity();
  @JsonIgnore public abstract String getEmail();
  @JsonIgnore public abstract String getPhone();
  @JsonIgnore public abstract boolean isActive();
}
