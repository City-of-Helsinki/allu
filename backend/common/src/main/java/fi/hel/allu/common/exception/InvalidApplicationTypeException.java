package fi.hel.allu.common.exception;

public class InvalidApplicationTypeException extends RuntimeException {
  public InvalidApplicationTypeException(String value) {
    super("Unknown application type: " + value);
  }
}
