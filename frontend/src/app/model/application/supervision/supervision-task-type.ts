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
   * Loppuvalvonta
   */
  FINAL_SUPERVISION = 'FINAL_SUPERVISION',
  /**
   * Takuuvalvonta
   */
  WARRANTY = 'WARRANTY'
}

export function isAutomaticSupervisionTaskType(type: SupervisionTaskType): boolean {
  return type === SupervisionTaskType.OPERATIONAL_CONDITION ||
         type === SupervisionTaskType.WARRANTY;
}
