package fi.hel.allu.model.service;

import java.util.Collections;
import java.util.List;

import fi.hel.allu.model.domain.InvoicingPeriod;

/**
 * Result of matching existing invoicing periods against the desired ones.
 *
 * <p>Only the periods that differ are inserted or deleted; periods whose dates and status match are
 * left untouched so that their identifiers, charge basis entries and invoice references stay
 * valid.</p>
 */
public class PeriodReconciliation {

  private final List<InvoicingPeriod> periodsToInsert;
  private final List<InvoicingPeriod> periodsToDelete;

  public PeriodReconciliation(List<InvoicingPeriod> periodsToInsert, List<InvoicingPeriod> periodsToDelete) {
    this.periodsToInsert = Collections.unmodifiableList(periodsToInsert);
    this.periodsToDelete = Collections.unmodifiableList(periodsToDelete);
  }

  public List<InvoicingPeriod> getPeriodsToInsert() {
    return periodsToInsert;
  }

  public List<InvoicingPeriod> getPeriodsToDelete() {
    return periodsToDelete;
  }

  public boolean hasChanges() {
    return !periodsToInsert.isEmpty() || !periodsToDelete.isEmpty();
  }
}
