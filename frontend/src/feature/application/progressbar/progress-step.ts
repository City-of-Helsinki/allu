import {ApplicationStatus} from '../../../model/application/application-status';
export enum ProgressStep {
  LOCATION,
  INFORMATION,
  SUMMARY,
  HANDLING,
  DECISION,
  MONITORING
}

/**
 * Determines progress step of application based on it's status
 * and whether the current page is summary or edit page
 * @param status current status of application
 * @param isSummary is true when current page is summary, otherwise false
 * @returns {ProgressStep}
 */
export function stepFrom(status: ApplicationStatus, isSummary: boolean): ProgressStep {
  switch (status) {
    case ApplicationStatus.DECISION:
      return ProgressStep.DECISION;
    case ApplicationStatus.HANDLING:
      return ProgressStep.HANDLING;
    default:
      if (isSummary) {
        return ProgressStep.SUMMARY;
      } else {
        return ProgressStep.INFORMATION;
      }
  }
}
