package fi.hel.allu.model.domain;

;

/*
 * Updating a record failed.
 */
public class UpdateException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public UpdateException(String message) {
    super(message);
  }

}
