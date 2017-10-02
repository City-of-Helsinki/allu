package fi.hel.allu.model.domain;

import java.time.temporal.ChronoUnit;

/**
 * ChargeBasisUnit specifies a unit for charge basis
 */
public enum ChargeBasisUnit {
  PIECE, SQUARE_METER, MULTIPLY,
  HOUR, DAY, WEEK, MONTH, YEAR;

  public static ChargeBasisUnit fromChronoUnit(ChronoUnit unit) {
    switch (unit) {
    case DAYS: return ChargeBasisUnit.DAY;
    case HOURS: return ChargeBasisUnit.HOUR;
    case WEEKS: return ChargeBasisUnit.WEEK;
    case MONTHS: return ChargeBasisUnit.MONTH;
    case YEARS: return ChargeBasisUnit.YEAR;
    // Unknown units are handled as pieces
    default: return ChargeBasisUnit.PIECE;
    }
  }
}
