import {StatusChangeInfo} from '@model/application/status-change-info';
import {CommentType} from '@model/application/comment/comment-type';

export class TerminationInfo extends StatusChangeInfo {
  constructor(
    public draft: boolean,
    public id?: number,
    public applicationId?: number,
    public type?: CommentType,
    public owner?: number,
    public creationTime?: Date,
    public expirationTime?: Date,
    public comment?: string,
    public terminationDecisionTime?: Date
  ) {
    super(type, comment, owner);
  }
}
