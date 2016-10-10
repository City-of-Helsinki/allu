package fi.hel.allu.common.exception;

/**
 * Exeption is thrown in case an entity or a value of an entity, which is meant to be unique in the system, is either created twice
 * or updated to collide with a another value in the system. In database terms, unique constraint violation.
 */
public class NonUniqueException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  private String uniqueValue;

  /**
   * Constructor.
   *
   * @param   message   Message describing the exception.
   */
  public NonUniqueException(String message) {
    super(message);
  }

  /**
   * Constructor.
   *
   * @param   message     Message describing the exception.
   * @param   uniqueValue Value that was used and which caused the exception to be thrown.
   */
  public NonUniqueException(String message, String uniqueValue) {
    super(message);
    this.uniqueValue = uniqueValue;
  }

  /**
   * @return  Value that was used and which caused the exception to be thrown.
   */
  public String getUniqueValue() {
    return uniqueValue;
  }

}
