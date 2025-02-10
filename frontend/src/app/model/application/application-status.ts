import {Application} from '@model/application/application';
import {ApplicationType} from '@model/application/type/application-type';
import {ArrayUtil} from '@util/array-util';
import {Some} from '@util/option';

export enum ApplicationStatus {
  PENDING_CLIENT = 'PENDING_CLIENT',
  PRE_RESERVED = 'PRE_RESERVED',
  PENDING = 'PENDING',
  WAITING_INFORMATION = 'WAITING_INFORMATION',
  INFORMATION_RECEIVED = 'INFORMATION_RECEIVED',
  HANDLING = 'HANDLING',
  NOTE = 'NOTE',
  RETURNED_TO_PREPARATION = 'RETURNED_TO_PREPARATION',
  WAITING_CONTRACT_APPROVAL = 'WAITING_CONTRACT_APPROVAL',
  DECISIONMAKING = 'DECISIONMAKING',
  DECISION = 'DECISION',
  REJECTED = 'REJECTED',
  OPERATIONAL_CONDITION = 'OPERATIONAL_CONDITION',
  TERMINATED = 'TERMINATED',
  FINISHED = 'FINISHED',
  CANCELLED = 'CANCELLED',
  REPLACED = 'REPLACED',
  ARCHIVED = 'ARCHIVED',
  ANONYMIZED = 'ANONYMIZED'
}

export const statusNames = Object.keys(ApplicationStatus);

export function applicationCanBeEdited(application: Application): boolean {
  const editableByStatus = Some(application.status)
    .map(status => editable.indexOf(status) >= 0)
    .orElse(true);
  const noPendingClientData = application.clientApplicationData === undefined;
  return editableByStatus && noPendingClientData;
}

export function invoicingChangesAllowedForType(application: Application): boolean {
  if (ArrayUtil.contains([ApplicationType.EXCAVATION_ANNOUNCEMENT, ApplicationType.AREA_RENTAL], application.type)) {
    return excavationOrAreaInvoicingChangeAllowed(application);
  } else {
    return invoicingChangesAllowed(application);
  }
}

export function invoicingChangesAllowed(application: Application): boolean {
  return applicationCanBeEdited(application);
}

export function excavationOrAreaInvoicingChangeAllowed(application: Application): boolean {
  const allowedForAll = invoicingChangesAllowed(application);
  const allowedForExcavationOrArea = ArrayUtil.contains(
      [ApplicationStatus.DECISION, ApplicationStatus.OPERATIONAL_CONDITION], application.status);
  return allowedForAll || allowedForExcavationOrArea;
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

export function isSameOrBetween(tested: ApplicationStatus, after: ApplicationStatus, before: ApplicationStatus): boolean {
  return isSameOrAfter(tested, after) && isSameOrBefore(tested, before);
}

export function isSameOrBefore(first: ApplicationStatus, second: ApplicationStatus): boolean {
  return !isAfter(first, second);
}

export function isSameOrAfter(first: ApplicationStatus, second: ApplicationStatus): boolean {
  return !isBefore(first, second);
}

export function compareTo(first: ApplicationStatus, second: ApplicationStatus): number {
  if (isAfter(second, first)) {
    return -1;
  } else if (isAfter(first, second)) {
    return 1;
  }
  return 0;
}

export const editable = [
  ApplicationStatus.PRE_RESERVED,
  ApplicationStatus.PENDING,
  ApplicationStatus.WAITING_INFORMATION,
  ApplicationStatus.HANDLING,
  ApplicationStatus.NOTE,
  ApplicationStatus.RETURNED_TO_PREPARATION,
];

export const searchable = [
  ApplicationStatus.PENDING_CLIENT,
  ApplicationStatus.PRE_RESERVED,
  ApplicationStatus.PENDING,
  ApplicationStatus.WAITING_INFORMATION,
  ApplicationStatus.INFORMATION_RECEIVED,
  ApplicationStatus.HANDLING,
  ApplicationStatus.NOTE,
  ApplicationStatus.RETURNED_TO_PREPARATION,
  ApplicationStatus.WAITING_CONTRACT_APPROVAL,
  ApplicationStatus.DECISIONMAKING,
  ApplicationStatus.DECISION,
  ApplicationStatus.REJECTED,
  ApplicationStatus.OPERATIONAL_CONDITION,
  ApplicationStatus.TERMINATED,
  ApplicationStatus.FINISHED,
  ApplicationStatus.CANCELLED,
  ApplicationStatus.ARCHIVED
];

export const workqueue_searchable = [
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
  ApplicationStatus.TERMINATED,
  ApplicationStatus.FINISHED,
  ApplicationStatus.CANCELLED
];

export const decided = [
  ApplicationStatus.DECISION,
  ApplicationStatus.REJECTED,
  ApplicationStatus.OPERATIONAL_CONDITION,
  ApplicationStatus.FINISHED,
  ApplicationStatus.TERMINATED,
  ApplicationStatus.ARCHIVED,
  ApplicationStatus.ANONYMIZED
];

export enum ApplicationStatusGroup {
  PRELIMINARY = 'PRELIMINARY',
  HANDLING = 'HANDLING',
  DECISION = 'DECISION',
  HISTORY = 'HISTORY'
}

export const distributionChangeAllowed = (status: ApplicationStatus) =>
  isSameOrBetween(status, ApplicationStatus.PRE_RESERVED, ApplicationStatus.ARCHIVED);
