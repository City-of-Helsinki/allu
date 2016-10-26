import {translations} from '../../util/translations';

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

export function translateStatus(status: ApplicationStatus) {
  return translations.application.status[ApplicationStatus[status]];
}

export class ApplicationStatusChange {
  constructor(public id: number, public status: ApplicationStatus, public comment: string) {}

  public static of(id: number, status: ApplicationStatus): ApplicationStatusChange {
    return new ApplicationStatusChange(id, status, undefined);
  }

  public static withComment(id: number, status: ApplicationStatus, comment: string): ApplicationStatusChange {
    return new ApplicationStatusChange(id, status, comment);
  }
}
