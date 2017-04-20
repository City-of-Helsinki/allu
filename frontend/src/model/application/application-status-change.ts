import {translations} from '../../util/translations';
import {ApplicationStatus} from './application-status';

// TODO: Might not contain all allowed changes yet
const legalChanges = [
  {currentStatus: ApplicationStatus.PRE_RESERVED, newStatus: [ApplicationStatus.PENDING]},
  {currentStatus: ApplicationStatus.PENDING, newStatus: [ApplicationStatus.HANDLING, ApplicationStatus.DECISIONMAKING]},
  {currentStatus: ApplicationStatus.HANDLING, newStatus: [ApplicationStatus.DECISIONMAKING]},
  {currentStatus: ApplicationStatus.DECISIONMAKING, newStatus: [
    ApplicationStatus.DECISION,
    ApplicationStatus.REJECTED,
    ApplicationStatus.RETURNED_TO_PREPARATION
  ]},
  {currentStatus: ApplicationStatus.DECISION, newStatus: [ApplicationStatus.FINISHED]}
];

export function translateStatus(status: ApplicationStatus) {
  return translations.application.status[ApplicationStatus[status]];
}

export class ApplicationStatusChange {
  constructor(public id: number, public status: ApplicationStatus, public comment?: string) {}

  public static of(id: number, status: ApplicationStatus): ApplicationStatusChange {
    return new ApplicationStatusChange(id, status, undefined);
  }

  public static withComment(id: number, status: ApplicationStatus, comment: string): ApplicationStatusChange {
    return new ApplicationStatusChange(id, status, comment);
  }

  public static legalChange(currentStatus: string, newStatus: string): boolean {
    if (currentStatus === newStatus) {
      return false;
    } else {
      return legalChanges
        .filter(change => ApplicationStatus[currentStatus] === change.currentStatus)
        .filter(change => change.newStatus.indexOf(ApplicationStatus[newStatus]) >= 0)
        .length > 0;
    }
  }
}
