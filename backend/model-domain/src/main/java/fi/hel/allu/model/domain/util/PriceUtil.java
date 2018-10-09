package fi.hel.allu.model.domain.util;

import fi.hel.allu.model.domain.ChargeBasisCalc;
import fi.hel.allu.model.domain.ChargeBasisEntry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class PriceUtil {
  /**
   * Calculate the total price of given charge basis entries
   *
   * @param chargeBasisEntries
   * @return total price in cents
   */
  public static int totalPrice(List<ChargeBasisEntry> chargeBasisEntries) {
    return new ChargeBasisCalc(chargeBasisEntries).toInvoiceRows().stream()
        .map(row -> BigDecimal.valueOf(row.getNetPrice()))
        .reduce((b1, b2) -> b1.add(b2)).orElse(BigDecimal.ZERO)
        .setScale(0, RoundingMode.UP).intValue();
  }
}
