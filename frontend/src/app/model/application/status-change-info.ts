import {CommentType} from './comment/comment-type';

export class StatusChangeInfo {
  constructor(
    public type?: CommentType,
    public comment?: string,
    public owner?: number) {
  }
}
