import {ApplicationStatus} from './application-status';

export class ApplicationStatusChange {
  constructor(public id: number, public status: ApplicationStatus, public comment?: string) {}

  public static of(id: number, status: ApplicationStatus): ApplicationStatusChange {
    return new ApplicationStatusChange(id, status, undefined);
  }
}
