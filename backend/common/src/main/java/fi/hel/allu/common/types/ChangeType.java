package fi.hel.allu.common.types;

public enum ChangeType {
  CREATED, // Application created
  STATUS_CHANGED, // Application status changed
  CONTENTS_CHANGED, // Application contents changed
  REPLACED, // Application replaced
  APPLICATION_ADDED, // Application added to a project
  APPLICATION_REMOVED, // Application removed from a project
  CUSTOMER_CHANGED,
  CONTACT_CHANGED
}
