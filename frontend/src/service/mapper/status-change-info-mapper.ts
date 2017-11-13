import {StatusChangeInfo} from '../../model/application/status-change-info';
import {CommentType} from '../../model/application/comment/comment-type';

export interface BackendStatusChangeInfo {
  type?: string;
  comment?: string;
  handler?: number;
}

export class StatusChangeInfoMapper {
  public static mapFrontEnd(changeInfo: StatusChangeInfo): BackendStatusChangeInfo {
    return changeInfo ? {
      type: CommentType[changeInfo.type],
      comment: changeInfo.comment,
      handler: changeInfo.handler
    } : undefined;
  }
}
