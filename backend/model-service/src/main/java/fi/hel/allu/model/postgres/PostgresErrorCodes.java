package fi.hel.allu.model.postgres;

/**
 * Postgres error codes.
 */
public enum PostgresErrorCodes {
  UNIQUE_VIOLATION(23505);

  public int getErrorCode() {
    return this.errorCode;
  }

  private int errorCode;

  private PostgresErrorCodes(int code) {
    this.errorCode = code;
  }

}
