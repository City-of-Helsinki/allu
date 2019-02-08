package fi.hel.allu.common.exception;

public class OptimisticLockException  extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public OptimisticLockException(String message) {
    super(message);
  }
}
