export enum ChangeType {
  CREATED = 'CREATED', // Application created
  STATUS_CHANGED = 'STATUS_CHANGED', // Application status changed
  CONTENTS_CHANGED = 'CONTENTS_CHANGED', // Application contents changed
  REPLACED = 'REPLACED', // Application replaced
  APPLICATION_ADDED = 'APPLICATION_ADDED', // Application added to a project
  APPLICATION_REMOVED = 'APPLICATION_REMOVED', // Application removed from a project
  CUSTOMER_CHANGED = 'CUSTOMER_CHANGED',
  CONTACT_CHANGED = 'CONTACT_CHANGED',
  LOCATION_CHANGED = 'LOCATION_CHANGED',
  OWNER_CHANGED = 'OWNER_CHANGED'
}
