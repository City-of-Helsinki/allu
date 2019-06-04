import {StatusChangeInfo} from '@model/application/status-change-info';
import {CommentType} from '@model/application/comment/comment-type';

export class TerminationInfo extends StatusChangeInfo {
  constructor(
    public id?: number,
    public applicationId?: number,
    public type?: CommentType,
    public owner?: number,
    public creationTime?: Date,
    public terminationTime?: Date,
    public comment?: string
  ) {
    super(type, comment, owner);
  }
}
