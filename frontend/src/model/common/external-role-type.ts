export enum ExternalRoleType {
  /**
   * External user belonging to "Allu ecosystem" i.e. is more like part of Allu than actual external user.
   */
  ROLE_INTERNAL,
  /**
   * External user who is a trusted partner. Trusted partner is allowed to do many, but not all operations in the external interface.
   */
  ROLE_TRUSTED_PARTNER
};
