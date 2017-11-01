package fi.hel.allu.model.pricing;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.InvoiceRow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Utility class for calculating from charge basis entries
 */
public class ChargeBasisCalc {
  /* Global multipliers don't refer to any tag, i.e. they refer to empty tag: */
  private static final String EMPTY_TAG = "";

  /* Simple entries (i.e., entries that don't alter some other) */
  private List<ChargeBasisEntry> simpleEntries = new ArrayList<>();

  /* Multiplying entries by tag (global or line-specific) */
  private Map<String, List<ChargeBasisEntry>> multiplierEntries = new HashMap<>();

  /**
   * Construct a new calculation instance. Goes through the given charge basis
   * entries and classifies them to either simple or multiplier ones.
   *
   * @param chargeBasisEntries entries for the calculation
   */
  public ChargeBasisCalc(Iterable<ChargeBasisEntry> chargeBasisEntries) {
    chargeBasisEntries.forEach(e -> {
      if (e.getUnit() == ChargeBasisUnit.MULTIPLY) {
        String referredTag = Optional.ofNullable(e.getReferredTag()).orElse(EMPTY_TAG);
        multiplierEntries.computeIfAbsent(referredTag, k -> new ArrayList<>()).add(e);
      } else {
        simpleEntries.add(e);
      }
    });
  }

  public List<InvoiceRow> toInvoiceRows() {
    List<InvoiceRow> invoiceRows = new ArrayList<>();
    BigDecimal totalPrice = BigDecimal.ZERO;
    for (ChargeBasisEntry se : simpleEntries) {
      invoiceRows.add(
          new InvoiceRow(se.getUnit(), se.getQuantity(), se.getText(), se.getExplanation(),
              se.getUnitPrice(),
              se.getNetPrice()));
      BigDecimal rowPrice = BigDecimal.valueOf(se.getNetPrice());
      if(se.getTag() != null && !se.getTag().isEmpty()) {
        rowPrice = addMultiplierRows(invoiceRows, se.getTag(), rowPrice, " ");
      }
      totalPrice = totalPrice.add(rowPrice);
    }
    addMultiplierRows(invoiceRows, EMPTY_TAG, totalPrice, "");
    return invoiceRows;
  }

  private BigDecimal addMultiplierRows(List<InvoiceRow> invoiceRows, String tag, BigDecimal totalPrice,
      String rowTextPrefix) {
    for (ChargeBasisEntry me : multiplierEntries.getOrDefault(tag, Collections.emptyList())) {
      final BigDecimal newTotalPrice = BigDecimal.valueOf(me.getQuantity()).multiply(totalPrice);
      final int diff = newTotalPrice.subtract(totalPrice).setScale(0, RoundingMode.UP).intValue();
      invoiceRows
          .add(new InvoiceRow(ChargeBasisUnit.PIECE, 1, me.getText(), me.getExplanation(), diff, diff));
      totalPrice = newTotalPrice;
    }
    return totalPrice;
  }
}
