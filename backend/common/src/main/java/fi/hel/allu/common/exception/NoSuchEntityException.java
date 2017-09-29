package fi.hel.allu.common.exception;

/*
 * Tried to access a non-existing entity
 */
public class NoSuchEntityException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private String missingEntityId;

  /*
   * Create with simply a message
   */
  public NoSuchEntityException(String message) {
    super(message);
  }

  /*
   * Create with message and missing entity's id
   */
  public NoSuchEntityException(String message, String missingEntityId) {
    super(message);
    this.missingEntityId = missingEntityId;
  }

  /*
 * Create with message and missing entity's id
 */
  public NoSuchEntityException(String message, int missingEntityId) {
    super(message);
    this.missingEntityId = Integer.toString(missingEntityId);
  }

  /*
   * The id of the entity that was tried to access
   */
  public String getMissingEntityId() {
    return missingEntityId;
  }
}
