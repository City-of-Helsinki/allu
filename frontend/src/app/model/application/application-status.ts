import {Application} from '@model/application/application';
import {ApplicationType} from '@model/application/type/application-type';

export enum ApplicationStatus {
  PENDING_CLIENT = 'PENDING_CLIENT',
  PRE_RESERVED = 'PRE_RESERVED',
  PENDING = 'PENDING',
  WAITING_INFORMATION = 'WAITING_INFORMATION',
  INFORMATION_RECEIVED = 'INFORMATION_RECEIVED',
  HANDLING = 'HANDLING',
  RETURNED_TO_PREPARATION = 'RETURNED_TO_PREPARATION',
  WAITING_CONTRACT_APPROVAL = 'WAITING_CONTRACT_APPROVAL',
  DECISIONMAKING = 'DECISIONMAKING',
  DECISION = 'DECISION',
  REJECTED = 'REJECTED',
  OPERATIONAL_CONDITION = 'OPERATIONAL_CONDITION',
  FINISHED = 'FINISHED',
  CANCELLED = 'CANCELLED',
  REPLACED = 'REPLACED',
  ARCHIVED = 'ARCHIVED'
}

export const statusNames = Object.keys(ApplicationStatus);

export function applicationCanBeEdited(application: Application): boolean {
  const editableByStatus = application.status === undefined ||
    isBetween(application.status, ApplicationStatus.PENDING_CLIENT, ApplicationStatus.WAITING_CONTRACT_APPROVAL);
  const noPendingClientData = application.clientApplicationData === undefined;
  return editableByStatus && noPendingClientData;
}

export function invoicingChangesAllowedForType(application: Application): boolean {
  if (ApplicationType.EXCAVATION_ANNOUNCEMENT === application.type) {
    return excavationInvoicingChangeAllowed(application);
  } else {
    return invoicingChangesAllowed(application);
  }
}

export function invoicingChangesAllowed(application: Application): boolean {
  return applicationCanBeEdited(application);
}

export function excavationInvoicingChangeAllowed(application: Application): boolean {
  const allowedForAll = invoicingChangesAllowed(application);
  const allowedForExcavation = [ApplicationStatus.DECISION, ApplicationStatus.OPERATIONAL_CONDITION].indexOf(application.status) >= 0;
  return allowedForAll || allowedForExcavation;
}

export function inHandling(status: ApplicationStatus): boolean {
  return [ApplicationStatus.HANDLING, ApplicationStatus.RETURNED_TO_PREPARATION].some(s => s === status);
}

export function isBefore(first: ApplicationStatus, second: ApplicationStatus): boolean {
  return statusNames.indexOf(first) < statusNames.indexOf(second);
}

export function isAfter(first: ApplicationStatus, second: ApplicationStatus): boolean {
  return statusNames.indexOf(first) > statusNames.indexOf(second);
}

export function isBetween(tested: ApplicationStatus, after: ApplicationStatus, before: ApplicationStatus): boolean {
  return isAfter(tested, after) && isBefore(tested, before);
}

export function isSameOrBefore(first: ApplicationStatus, second: ApplicationStatus): boolean {
  return !isAfter(first, second);
}

export function isSameOrAfter(first: ApplicationStatus, second: ApplicationStatus): boolean {
  return !isBefore(first, second);
}

export function contains(included: ApplicationStatus[], tested: ApplicationStatus): boolean {
  return included.indexOf(tested) >= 0;
}

export const searchable = [
  ApplicationStatus.PENDING_CLIENT,
  ApplicationStatus.PRE_RESERVED,
  ApplicationStatus.PENDING,
  ApplicationStatus.WAITING_INFORMATION,
  ApplicationStatus.INFORMATION_RECEIVED,
  ApplicationStatus.HANDLING,
  ApplicationStatus.RETURNED_TO_PREPARATION,
  ApplicationStatus.WAITING_CONTRACT_APPROVAL,
  ApplicationStatus.DECISIONMAKING,
  ApplicationStatus.DECISION,
  ApplicationStatus.REJECTED,
  ApplicationStatus.OPERATIONAL_CONDITION,
  ApplicationStatus.FINISHED,
  ApplicationStatus.CANCELLED,
  ApplicationStatus.ARCHIVED
];

export const workqueue_searchable = [
  ApplicationStatus.PRE_RESERVED,
  ApplicationStatus.PENDING,
  ApplicationStatus.WAITING_INFORMATION,
  ApplicationStatus.INFORMATION_RECEIVED,
  ApplicationStatus.HANDLING,
  ApplicationStatus.RETURNED_TO_PREPARATION,
  ApplicationStatus.WAITING_CONTRACT_APPROVAL,
  ApplicationStatus.DECISIONMAKING,
  ApplicationStatus.DECISION,
  ApplicationStatus.REJECTED,
  ApplicationStatus.OPERATIONAL_CONDITION,
  ApplicationStatus.FINISHED,
  ApplicationStatus.CANCELLED
];

export const decided = [
  ApplicationStatus.DECISION,
  ApplicationStatus.REJECTED,
  ApplicationStatus.OPERATIONAL_CONDITION,
  ApplicationStatus.FINISHED,
  ApplicationStatus.ARCHIVED
];

export enum ApplicationStatusGroup {
  PRELIMINARY,
  HANDLING,
  DECISION,
  HISTORY
}
