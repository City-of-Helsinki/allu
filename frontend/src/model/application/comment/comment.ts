import {User} from '../../common/user';
import {TimeUtil} from '../../../util/time.util';

export class Comment {
  constructor()
  constructor(
    id: number,
    type: string,
    text: string,
    createTime: Date,
    updateTime: Date,
    user: User
  )
  constructor(
    public id?: number,
    public type?: string,
    public text?: string,
    public createTime?: Date,
    public updateTime?: Date,
    public user?: User
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
}
