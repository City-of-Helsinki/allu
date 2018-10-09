package fi.hel.allu.model.domain;

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
  /* Global changers don't refer to any tag, i.e. they refer to empty tag: */
  private static final String EMPTY_TAG = "";

  /* Simple entries (i.e., entries that don't alter some other) */
  private List<ChargeBasisEntry> simpleEntries = new ArrayList<>();

  /* Percent change entries by tag (global or line-specific) */
  private Map<String, List<ChargeBasisEntry>> percentEntries = new HashMap<>();

  /**
   * Construct a new calculation instance. Goes through the given charge basis
   * entries and classifies them to either simple or multiplier ones.
   *
   * @param chargeBasisEntries entries for the calculation
   */
  public ChargeBasisCalc(Iterable<ChargeBasisEntry> chargeBasisEntries) {
    chargeBasisEntries.forEach(e -> {
      if (e.getUnit() == ChargeBasisUnit.PERCENT) {
        String referredTag = Optional.ofNullable(e.getReferredTag()).orElse(EMPTY_TAG);
        percentEntries.computeIfAbsent(referredTag, k -> new ArrayList<>()).add(e);
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
          new InvoiceRow(se.getId(), se.getUnit(), se.getQuantity(), se.getText(), se.getExplanation(),
              se.getUnitPrice(),
              se.getNetPrice()));
      BigDecimal rowPrice = BigDecimal.valueOf(se.getNetPrice());
      if(se.getTag() != null && !se.getTag().isEmpty()) {
        rowPrice = addPercentRows(invoiceRows, se.getTag(), rowPrice, " ");
      }
      totalPrice = totalPrice.add(rowPrice);
    }
    addPercentRows(invoiceRows, EMPTY_TAG, totalPrice, "");
    return invoiceRows;
  }

  private BigDecimal addPercentRows(List<InvoiceRow> invoiceRows, String tag, BigDecimal totalPrice,
      String rowTextPrefix) {
    for (ChargeBasisEntry me : percentEntries.getOrDefault(tag, Collections.emptyList())) {
      final BigDecimal newTotalPrice = BigDecimal.valueOf((100.0 + me.getQuantity()) / 100.0).multiply(totalPrice);
      final int diff = newTotalPrice.subtract(totalPrice).setScale(0, RoundingMode.UP).intValue();
      invoiceRows
          .add(new InvoiceRow(me.getId(), ChargeBasisUnit.PIECE, 1, me.getText(), me.getExplanation(), diff, diff));
      totalPrice = newTotalPrice;
    }
    return totalPrice;
  }
}
