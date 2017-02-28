import {translations} from '../../util/translations';
import {ApplicationStatus} from './application-status';

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
}
