package fi.hel.allu.common.types;

/**
 * List of attachment types.
 */
public enum AttachmentType {
  ADDED_BY_CUSTOMER(false),   // Attachment added by the customer
  ADDED_BY_HANDLER(false),    // Attachment added by the handler (HKR employee)
  DEFAULT(true),              // Application type specific default attachment
  DEFAULT_IMAGE(true),        // Application type specific default image attachment such as picture of traffic arrangements
  DEFAULT_TERMS(true),        // Application type specific default terms attachment
  SUPERVISION(false),         // Supervision attachment
  STATEMENT(false),           // Statement (lausunto) attachment
  OTHER(false);

  private boolean isDefaultAttachment;

  private AttachmentType(boolean isDefaultAttachment) {
    this.isDefaultAttachment = isDefaultAttachment;
  }

  public boolean isDefaultAttachment() {
    return isDefaultAttachment;
  }
}
