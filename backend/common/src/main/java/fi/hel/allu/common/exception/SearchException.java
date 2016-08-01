package fi.hel.allu.common.exception;

public class SearchException  extends RuntimeException {
  /**
   * Version identifier for serialization
   */
  private static final long serialVersionUID = 1L;

  /*
   * Create with simply a message
   */
  public SearchException(String message) {
    super(message);
  }

  /*
   * Create with a cause
   */
  public SearchException(Throwable thr) {
    super(thr);
  }
}