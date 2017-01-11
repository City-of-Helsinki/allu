import {Injectable} from '@angular/core';
import '../../../rxjs-extensions.ts';

import {Comment} from '../../../model/application/comment/comment';
import {CommentService} from './comment.service';

@Injectable()
export class CommentHub {

  constructor(private commentService: CommentService) {}

  /**
   * Saves comment to given application
   */
  public saveComment = (applicationId: number, comment: Comment) => this.commentService.save(applicationId, comment);

  /**
   * Removes comment with id
   */
  public removeComment = (id: number) => this.commentService.remove(id);

  /**
   * Fetches all comments belonging to given application
   */
  public getComments = (applicationId: number) => this.commentService.getComments(applicationId);
}
