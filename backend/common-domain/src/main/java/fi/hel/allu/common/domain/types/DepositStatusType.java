package fi.hel.allu.common.domain.types;

import java.util.Optional;

/**
 * Deposit related application tag types
 */
public enum DepositStatusType {

  UNPAID_DEPOSIT(ApplicationTagType.DEPOSIT_REQUESTED),
  PAID_DEPOSIT(ApplicationTagType.DEPOSIT_PAID),
  RETURNED_DEPOSIT(null);

  public final Optional<ApplicationTagType> tag;

  private DepositStatusType(ApplicationTagType tag) {
    this.tag = Optional.ofNullable(tag);
  }
}
