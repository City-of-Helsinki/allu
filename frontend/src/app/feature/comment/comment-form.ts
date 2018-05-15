import {User} from '../../model/user/user';
import {Comment} from '../../model/application/comment/comment';

export class CommentForm {
  constructor(
    public id?: number,
    public type?: string,
    public text?: string,
    public createTime?: Date,
    public updateTime?: Date,
    public user?: User
  ) {}

  static from(comment: Comment): CommentForm {
    const form = new CommentForm();
    form.id = comment.id;
    form.type = comment.type || 'INTERNAL';
    form.text = comment.text;
    form.createTime = comment.createTime;
    form.updateTime = comment.updateTime;
    form.user = comment.user;
    return form;
  }

  static to(form: CommentForm): Comment {
    const comment = new Comment();
    comment.id = form.id;
    comment.type = form.type;
    comment.text = form.text;
    comment.createTime = form.createTime;
    comment.updateTime = form.updateTime;
    comment.user = form.user;
    return comment;
  }
}
