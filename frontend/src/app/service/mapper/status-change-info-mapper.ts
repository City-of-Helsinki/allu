import {StatusChangeInfo} from '../../model/application/status-change-info';
import {CommentType} from '../../model/application/comment/comment-type';

export interface BackendStatusChangeInfo {
  type?: CommentType;
  comment?: string;
  owner?: number;
}

export class StatusChangeInfoMapper {
  public static mapFrontEnd(changeInfo: StatusChangeInfo): BackendStatusChangeInfo {
    return changeInfo ? {
      type: changeInfo.type,
      comment: changeInfo.comment,
      owner: changeInfo.owner
    } : undefined;
  }
}
