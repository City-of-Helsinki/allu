package fi.hel.allu.common.domain.types;

public enum RoleType {
  /**
   * Allowed to create applications.
   */
  ROLE_CREATE_APPLICATION,
  /**
   * Allowed to process applications.
   */
  ROLE_PROCESS_APPLICATION,
  /**
   * Allowed to make decisions.
   */
  ROLE_DECISION,
  /**
   * Allowed to supervise.
   */
  ROLE_SUPERVISE,
  /**
   * Allowed to check invoices.
   */
  ROLE_INVOICING,
  /**
   * Allowed to comment applications and projects and to add attachments.
   */
  ROLE_DECLARANT,
  /**
   * Allowed to login and view applications.
   */
  ROLE_VIEW,
  /**
   * Allowed to change system configuration.
   */
  ROLE_ADMIN;
}
