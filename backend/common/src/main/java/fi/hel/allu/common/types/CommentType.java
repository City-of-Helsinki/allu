package fi.hel.allu.common.types;

/**
 * Valid comment types for application comments
 */
public enum CommentType {
  INVOICING(true),
  RETURN(false),
  REJECT(true),
  INTERNAL(true),
  PROPOSE_APPROVAL(false),
  PROPOSE_REJECT(false),
  PROPOSE_TERMINATION(false),
  EXTERNAL_SYSTEM(false),
  TO_EXTERNAL_SYSTEM(true);

  private final boolean manuallyAdded;

  private CommentType(boolean manuallyAdded) {
    this.manuallyAdded = manuallyAdded;
  }

  public boolean isManuallyAdded() {
    return manuallyAdded;
  }
}
