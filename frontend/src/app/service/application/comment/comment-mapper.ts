import {Comment} from '../../../model/application/comment/comment';
import {BackendUser} from '../../backend-model/backend-user';
import {TimeUtil} from '../../../util/time.util';
import {UserMapper} from '../../mapper/user-mapper';
import {Some} from '../../../util/option';

export class CommentMapper {
  public static mapBackendList(comments: Array<BackendComment>): Array<Comment> {
    return (comments)
      ? comments.map(comment => CommentMapper.mapBackend(comment))
      : [];
  }

  static mapBackend(backendComment: BackendComment): Comment {
    return new Comment(
      backendComment.id,
      backendComment.type,
      backendComment.text,
      TimeUtil.dateFromBackend(backendComment.createTime),
      TimeUtil.dateFromBackend(backendComment.updateTime),
      UserMapper.mapBackend(backendComment.user)
    );
  }

  static mapFrontend(comment: Comment): any {
    return (comment) ?
      {
        id: comment.id,
        type: comment.type,
        text: comment.text,
        createTime: Some(comment.createTime).map(createTime => createTime.toISOString()).orElse(undefined),
        updateTime: Some(comment.updateTime).map(updateTime => updateTime.toISOString()).orElse(undefined),
        user: UserMapper.mapFrontend(comment.user)
      }
      : undefined;
  }
}

export interface BackendComment {
  id: number;
  type: string;
  text: string;
  createTime: string;
  updateTime: string;
  user: BackendUser;
}
