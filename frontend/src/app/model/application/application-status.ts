export enum ApplicationStatus {
  PENDING_CLIENT,
  PRE_RESERVED,
  PENDING,
  WAITING_INFORMATION,
  INFORMATION_RECEIVED,
  HANDLING,
  RETURNED_TO_PREPARATION,
  WAITING_CONTRACT_APPROVAL,
  DECISIONMAKING,
  DECISION,
  REJECTED,
  FINISHED,
  CANCELLED,
  REPLACED,
  ARCHIVED
}

export function applicationCanBeEdited(status: ApplicationStatus): boolean {
  return status ? status < ApplicationStatus.DECISIONMAKING : true;
}

export function inHandling(status: ApplicationStatus): boolean {
  return [ApplicationStatus.HANDLING, ApplicationStatus.RETURNED_TO_PREPARATION].some(s => s === status);
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
  ApplicationStatus.FINISHED,
  ApplicationStatus.CANCELLED
];

export const decided = [
  ApplicationStatus.DECISION,
  ApplicationStatus.REJECTED,
  ApplicationStatus.FINISHED,
  ApplicationStatus.ARCHIVED
];

export enum ApplicationStatusGroup {
  PRELIMINARY,
  HANDLING,
  DECISION,
  HISTORY
}
