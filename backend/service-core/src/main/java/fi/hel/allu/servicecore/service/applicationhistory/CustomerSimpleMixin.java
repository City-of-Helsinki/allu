package fi.hel.allu.servicecore.service.applicationhistory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.hel.allu.common.types.CustomerType;
import fi.hel.allu.servicecore.domain.PostalAddressJson;

/**
 * Ignored fields when serializing Customer for history comparisons
 */
public abstract class CustomerSimpleMixin {
  @JsonIgnore public abstract CustomerType getType();
  @JsonIgnore public abstract PostalAddressJson getPostalAddress();
  @JsonIgnore public abstract String getEmail();
  @JsonIgnore public abstract String getPhone();
  @JsonIgnore public abstract String getRegistryKey();
  @JsonIgnore public abstract boolean isActive();
}
