package fi.hel.allu.model.service.history;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Jackson mixins to exclude database identity fields from customer history comparisons.
 * Follows the same pattern as applicationhistory mixins in service-core.
 */
public class CustomerHistoryMixins {

  public static abstract class CustomerMixin {
    @JsonIgnore public abstract Integer getId();
  }

  public static abstract class ContactMixin {
    @JsonIgnore public abstract Integer getId();
    @JsonIgnore public abstract Integer getCustomerId();
  }

  public static abstract class PostalAddressMixin {
    @JsonIgnore public abstract Integer getId();
  }
}
