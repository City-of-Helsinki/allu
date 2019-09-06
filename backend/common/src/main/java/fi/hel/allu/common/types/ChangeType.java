package fi.hel.allu.common.types;

public enum ChangeType {
  CREATED, // Application created
  STATUS_CHANGED, // Application status changed
  CONTENTS_CHANGED, // Application contents changed
  REPLACED, // Application replaced
  APPLICATION_ADDED, // Application added to a project
  APPLICATION_REMOVED, // Application removed from a project
  CUSTOMER_CHANGED,
  CONTACT_CHANGED,
  LOCATION_CHANGED,
  OWNER_CHANGED,
  CONTRACT_STATUS_CHANGED,
  COMMENT_ADDED,
  COMMENT_REMOVED,
  ATTACHMENT_ADDED,
  ATTACHMENT_REMOVED,
  SUPERVISION_ADDED,
  SUPERVISION_APPROVED,
  SUPERVISION_REJECTED,
  SUPERVISION_REMOVED
}
