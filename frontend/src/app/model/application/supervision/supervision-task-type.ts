export enum SupervisionTaskType {
  /**
   * Aloitusvalvonta
   */
  PRELIMINARY_SUPERVISION = 'PRELIMINARY_SUPERVISION',
  /**
   * Talvityön toiminnallinen kunto
   */
  OPERATIONAL_CONDITION = 'OPERATIONAL_CONDITION',
  /**
   * Valvonta
   */
  SUPERVISION = 'SUPERVISION',
  /**
   * Työnaikainen valvonta
   */
  WORK_TIME_SUPERVISION = 'WORK_TIME_SUPERVISION',
  /**
   * Loppuvalvonta
   */
  FINAL_SUPERVISION = 'FINAL_SUPERVISION',
  /**
   * Takuuvalvonta
   */
  WARRANTY = 'WARRANTY'
}

export function isAutomaticSupervisionTaskType(type: SupervisionTaskType): boolean {
  return [
    SupervisionTaskType.OPERATIONAL_CONDITION,
    SupervisionTaskType.WARRANTY,
    SupervisionTaskType.WORK_TIME_SUPERVISION
  ].indexOf(type) >= 0;
}
