export enum ApplicationStatus {
  PRE_RESERVED,
  PENDING,
  HANDLING,
  RETURNED_TO_PREPARATION,
  DECISIONMAKING,
  DECISION,
  REJECTED,
  FINISHED,
  CANCELLED
}

export function canBeEdited(status: ApplicationStatus): boolean {
  return status < ApplicationStatus.DECISIONMAKING;
}

export function inHandling(status: ApplicationStatus): boolean {
  return [ApplicationStatus.HANDLING, ApplicationStatus.RETURNED_TO_PREPARATION].some(s => s === status);
}
