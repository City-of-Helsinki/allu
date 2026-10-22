package fi.hel.allu.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.InvoicingPeriod;

/**
 * Matches existing invoicing periods against desired ones.
 *
 * <p>A period is kept when its start date, end date and status match a desired period. Kept periods
 * retain their identifiers, so charge basis entries and invoice references pointing at them stay
 * valid. Only periods that do not match are replaced.</p>
 */
public final class InvoicingPeriodReconciler {

  private InvoicingPeriodReconciler() {
  }

  /**
   * Compares the existing periods with the desired ones.
   *
   * <p>Desired periods without a matching existing period are inserted. Existing periods without a
   * matching desired period are deleted. Periods that match are left alone.</p>
   *
   * @param currentPeriods the periods currently stored for the application
   * @param desiredPeriods the periods the application should have
   * @return the insertions and deletions needed to reach the desired state
   */
  public static PeriodReconciliation reconcile(List<InvoicingPeriod> currentPeriods,
      List<InvoicingPeriod> desiredPeriods) {
    List<InvoicingPeriod> remaining = new ArrayList<>(currentPeriods);
    List<InvoicingPeriod> toInsert = new ArrayList<>();

    for (InvoicingPeriod desired : desiredPeriods) {
      Optional<InvoicingPeriod> match = remaining.stream()
          .filter(current -> isSamePeriod(current, desired))
          .findFirst();
      if (match.isPresent()) {
        remaining.remove(match.get());
      } else {
        toInsert.add(desired);
      }
    }

    return new PeriodReconciliation(toInsert, remaining);
  }

  /**
   * Returns true when the two periods cover the same dates and belong to the same phase.
   */
  public static boolean isSamePeriod(InvoicingPeriod current, InvoicingPeriod desired) {
    return TimeUtil.isSameDate(current.getStartTime(), desired.getStartTime())
        && TimeUtil.isSameDate(current.getEndTime(), desired.getEndTime())
        && current.getInvoicableStatus() == desired.getInvoicableStatus();
  }
}
