export enum RoleType {
  /**
   * Allowed to create applications.
   */
  ROLE_CREATE_APPLICATION = 'ROLE_CREATE_APPLICATION',
  /**
   * Allowed to process applications.
   */
  ROLE_PROCESS_APPLICATION = 'ROLE_PROCESS_APPLICATION',
  /**
   * Allowed to make decisions.
   */
  ROLE_DECISION = 'ROLE_DECISION',
  /**
   * Allowed to supervise.
   */
  ROLE_SUPERVISE = 'ROLE_SUPERVISE',
  /**
   * Allowed to check invoices.
   */
  ROLE_INVOICING = 'ROLE_INVOICING',
  /**
   * Allowed to login and view applications
   */
  ROLE_VIEW = 'ROLE_VIEW',
  /**
   * Allowed to change system configuration.
   */
  ROLE_ADMIN = 'ROLE_ADMIN'
}

// Roles who can modify Allu info
export const MODIFY_ROLES = [
  RoleType.ROLE_CREATE_APPLICATION,
  RoleType.ROLE_PROCESS_APPLICATION,
  RoleType.ROLE_DECISION,
  RoleType.ROLE_SUPERVISE,
  RoleType.ROLE_INVOICING,
  RoleType.ROLE_ADMIN
];
