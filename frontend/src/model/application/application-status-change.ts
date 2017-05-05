import {ApplicationStatus} from './application-status';
import {StatusChangeComment} from './status-change-comment';

export class ApplicationStatusChange {
  constructor(public id: number, public status: ApplicationStatus, public comment?: StatusChangeComment) {}
}
