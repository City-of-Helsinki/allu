import {User} from '../../user/user';
import {TimeUtil} from '../../../util/time.util';
import {CommentType} from './comment-type';

export class Comment {
  constructor(
    public id?: number,
    public type?: string,
    public text?: string,
    public createTime?: Date,
    public updateTime?: Date,
    public user?: User,
    public commentator?: string
  ) {}

  copy(): Comment {
    return new Comment(
      this.id,
      this.type,
      this.text,
      this.createTime,
      this.updateTime,
      this.user
    );
  }

  get uiCreateTime(): string {
    return TimeUtil.getUiDateTimeString(this.createTime);
  }

  get uiUpdateTime(): string {
    return TimeUtil.getUiDateTimeString(this.updateTime);
  }

  get typeEnum() {
    return this.type ? CommentType[this.type] : undefined;
  }

  set typeEnum(type: CommentType) {
    this.type = CommentType[type];
  }
}
