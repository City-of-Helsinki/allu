package fi.hel.allu.servicecore.service.applicationhistory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.hel.allu.common.domain.types.CustomerRoleType;

/**
 * Ignored fields when serializing CustomerWithContacts for history comparisons
 */
public abstract class CustomerWithContactsSimpleMixin {
  @JsonIgnore public abstract CustomerRoleType getRoleType();
}
