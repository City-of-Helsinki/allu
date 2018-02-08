export enum SupervisionTaskType {
  /**
   * Aloitusvalvonta
   */
  PRELIMINARY_SUPERVISION,
  /**
   * Talvityön toiminnallinen kunto
   */
  OPERATIONAL_CONDITION,
  /**
   * Valvonta
   */
  SUPERVISION,
  /**
   * Loppuvalvonta
   */
  FINAL_SUPERVISION,
  /**
   * Takuuvalvonta
   */
  WARRANTY
}

export function isAutomaticSupervisionTaskType(type: SupervisionTaskType): boolean {
  return type === SupervisionTaskType.OPERATIONAL_CONDITION ||
         type === SupervisionTaskType.WARRANTY;
}
