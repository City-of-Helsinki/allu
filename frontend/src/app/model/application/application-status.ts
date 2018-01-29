export enum ApplicationStatus {
  PRE_RESERVED,
  PENDING,
  HANDLING,
  RETURNED_TO_PREPARATION,
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
  ApplicationStatus.PRE_RESERVED,
  ApplicationStatus.PENDING,
  ApplicationStatus.HANDLING,
  ApplicationStatus.RETURNED_TO_PREPARATION,
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
  ApplicationStatus.HANDLING,
  ApplicationStatus.RETURNED_TO_PREPARATION,
  ApplicationStatus.DECISIONMAKING,
  ApplicationStatus.DECISION,
  ApplicationStatus.REJECTED,
  ApplicationStatus.FINISHED,
  ApplicationStatus.CANCELLED
];
