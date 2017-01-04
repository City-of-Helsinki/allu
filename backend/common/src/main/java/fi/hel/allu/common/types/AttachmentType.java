package fi.hel.allu.common.types;

/**
 * List of attachment types.
 */
public enum AttachmentType {
  ADDED_BY_CUSTOMER,  // Attachment added by the customer
  ADDED_BY_HANDLER,   // Attachment added by the handler (HKR employee)
  DEFAULT,            // Application type specific default attachment
  DEFAULT_IMAGE,      // Application type specific default image attachment such as picture of traffic arrangements
  DEFAULT_TERMS       // Application type specific default terms attachment
}
