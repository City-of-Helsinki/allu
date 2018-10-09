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

export function applicationCanBeEdited(status: ApplicationStatus): boolean {
  if (status !== undefined) {
    return isAfter(status, ApplicationStatus.PENDING_CLIENT) && isBefore(status, ApplicationStatus.WAITING_CONTRACT_APPROVAL);
  } else {
    return true;
  }
}

export function invoicingChangesAllowedForType(type: ApplicationType, status: ApplicationStatus): boolean {
  if (ApplicationType.EXCAVATION_ANNOUNCEMENT === type) {
    return excavationInvoicingChangeAllowed(status);
  } else {
    return invoicingChangesAllowed(status);
  }
}

export function invoicingChangesAllowed(status): boolean {
  return applicationCanBeEdited(status);
}

export function excavationInvoicingChangeAllowed(status: ApplicationStatus): boolean {
  const invoicingChangesAllowedForAll = invoicingChangesAllowed(status);
  const invoicingChangesAllowedForExcavation = [ApplicationStatus.DECISION, ApplicationStatus.OPERATIONAL_CONDITION].indexOf(status) >= 0;
  return invoicingChangesAllowedForAll || invoicingChangesAllowedForExcavation;
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
