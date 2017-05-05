import {CommentType} from './comment/comment-type';
import {ApplicationStatus} from './application-status';

export class StatusChangeComment {
  constructor(public type?: CommentType, public comment?: string) {}

  public static fromStatus(status: ApplicationStatus, commentText?: string): StatusChangeComment {
    switch (status) {
      case ApplicationStatus.DECISION:
        return new StatusChangeComment(undefined, commentText);
      case ApplicationStatus.REJECTED:
        return new StatusChangeComment(CommentType.REJECT, commentText);
      default:
        throw new Error('Invalid application status ' + ApplicationStatus[status] + ' for mapping to comment type');
    }
  }
}
