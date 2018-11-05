package fi.hel.allu.common.domain.types;

public enum SupervisionTaskType {
  /**
   * Aloitusvalvonta
   */
  PRELIMINARY_SUPERVISION,
  /**
   * Toiminnallisen kunnon valvonta
   */
  OPERATIONAL_CONDITION,
  /**
   * Valvonta
   */
  SUPERVISION,
  /**
   * Työnaikainen valvonta
   */
  WORK_TIME_SUPERVISION,
  /**
   * Loppuvalvonta
   */
  FINAL_SUPERVISION,
  /**
   * Takuuvalvonta
   */
  WARRANTY
}
