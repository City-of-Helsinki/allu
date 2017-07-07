package fi.hel.allu.common.domain.types;

/**
 * Enumeration of all customer role types supported by Allu. A customer may have different roles on different applications.
 */
public enum CustomerRoleType {
  /** In Finnish: Hakija */
  APPLICANT,
  /** In Finnish: Rakennuttaja */
  PROPERTY_DEVELOPER,
  /** In Finnish: Työn suorittaja / urakoitsija */
  CONTRACTOR,
  /** In Finnish: Asiamies */
  REPRESENTATIVE
}
