package fi.hel.allu.common.domain.types;

public enum SupervisionTaskType {
  /**
   * Aloitusvalvonta
   */
  PRELIMINARY_SUPERVISION(true),
  /**
   * Toiminnallisen kunnon valvonta
   */
  OPERATIONAL_CONDITION(false),
  /**
   * Valvonta
   */
  SUPERVISION(true),
  /**
   * Työnaikainen valvonta
   */
  WORK_TIME_SUPERVISION(false),
  /**
   * Loppuvalvonta
   */
  FINAL_SUPERVISION(true),
  /**
   * Takuuvalvonta
   */
  WARRANTY(false);

  private final boolean manuallyAdded;

  private SupervisionTaskType(boolean manuallyAdded) {
    this.manuallyAdded = manuallyAdded;
  }

  public boolean isManuallyAdded()  {
    return manuallyAdded;
  }

}
