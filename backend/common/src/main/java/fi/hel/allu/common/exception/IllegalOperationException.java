package fi.hel.allu.common.exception;

/**
 * Exeption is thrown in case where operation was tried even though current state does not allow it.
 * For example user tried to update application which was cancelled.
 */
public class IllegalOperationException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  /**
   * Constructor.
   *
   * @param   message   Message describing the exception.
   */
  public IllegalOperationException(String message) {
    super(message);
  }
}
