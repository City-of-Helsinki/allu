package fi.hel.allu.model.postgres;

import org.postgresql.util.PSQLException;

/**
 * Decodes SQL exceptions thrown by Spring.
 */
public class ExceptionResolver {
  public static boolean isUniqueConstraintViolation(Exception e) {
    if (e.getCause() instanceof PSQLException) {
      PSQLException psqlException = (PSQLException) e.getCause();
      try {
        int errorCode = Integer.parseInt(new String(psqlException.getSQLState().getBytes()));
        if (errorCode == PostgresErrorCodes.UNIQUE_VIOLATION.getErrorCode()) {
          return true;
        }
      } catch (NumberFormatException nfe) {
        // do nothing
      }
    }
    return false;
  }
}
