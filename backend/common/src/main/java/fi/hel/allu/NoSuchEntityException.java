package fi.hel.allu;

/*
 * Tried to access a non-existing entity
 */
public class NoSuchEntityException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public NoSuchEntityException(String message) {
    super(message);
  }

}
