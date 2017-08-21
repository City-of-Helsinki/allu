package fi.hel.allu.servicecore.service.applicationhistory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.hel.allu.servicecore.domain.UserJson;

/**
 * Ignored fields when serializing Application for history comparisons
 */
public abstract class ApplicationMixIn {
  @JsonIgnore public abstract UserJson getHandler();
}
