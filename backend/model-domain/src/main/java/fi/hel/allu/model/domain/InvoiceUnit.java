package fi.hel.allu.model.domain;

import java.time.temporal.ChronoUnit;

/**
 * InvoiceUnit specifies a unit for invoice row
 */
public enum InvoiceUnit {
  PIECE, SQUARE_METER, MULTIPLY,
  HOUR, DAY, WEEK, MONTH, YEAR;

  public static InvoiceUnit fromChronoUnit(ChronoUnit unit) {
    switch (unit) {
    case DAYS: return InvoiceUnit.DAY;
    case HOURS: return InvoiceUnit.HOUR;
    case WEEKS: return InvoiceUnit.WEEK;
    case MONTHS: return InvoiceUnit.MONTH;
    case YEARS: return InvoiceUnit.YEAR;
    // Unknown units are handled as pieces
    default: return InvoiceUnit.PIECE;
    }
  }
}
