import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';

import {Comment} from '../../../model/application/comment/comment';
import {BackendComment, CommentMapper} from './comment-mapper';
import {ErrorHandler} from '../../error/error-handler.service';
import {findTranslation} from '../../../util/translations';
import {NumberUtil} from '../../../util/number.util';
import {CommentTargetType} from '../../../model/application/comment/comment-target-type';

const COMMENTS_URL = '/api/comments';

@Injectable()
export class CommentService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {}

  getCommentsFor(target: CommentTargetType, id: number): Observable<Array<Comment>> {
    const url = this.urlByTarget(target, id);
    return this.getComments(url);
  }

  insertFor(target: CommentTargetType, id: number, comment: Comment): Observable<Comment> {
    const url = this.urlByTarget(target, id);
    return this.insertComment(url, comment);
  }

  saveComment(target: CommentTargetType, id: number, comment: Comment): Observable<Comment> {
    return NumberUtil.isDefined(comment.id)
      ? this.updateComment(comment)
      : this.insertFor(target, id, comment);
  }

  remove(id: number): Observable<{}> {
    const url = `${COMMENTS_URL}/${id}`;
    return this.http.delete(url)
      .catch(error => this.errorHandler.handle(error, findTranslation('comment.error.remove')));
  }

  private getComments(url: string): Observable<Comment[]> {
    return this.http.get<BackendComment[]>(url)
      .map(comments => comments.map(comment => CommentMapper.mapBackend(comment)))
      .catch(error => this.errorHandler.handle(error, findTranslation('comment.error.fetch')));
  }

  private insertComment(url: string, comment: Comment): Observable<Comment> {
    return this.http.post<BackendComment>(url,
      JSON.stringify(CommentMapper.mapFrontend(comment)))
      .map(saved => CommentMapper.mapBackend(saved))
      .catch(error => this.errorHandler.handle(error, findTranslation('comment.error.save')));
  }

  private updateComment(comment: Comment): Observable<Comment> {
    const url = `${COMMENTS_URL}/${comment.id}`;
    return this.http.put<BackendComment>(url,
      JSON.stringify(CommentMapper.mapFrontend(comment)))
      .map(saved => CommentMapper.mapBackend(saved))
      .catch(error => this.errorHandler.handle(error, findTranslation('comment.error.save')));
  }

  private urlByTarget(target: CommentTargetType, id: number): string {
    return target === CommentTargetType.Application
      ? this.applicationCommentsUrl(id)
      : this.projectCommentsUrl(id);
  }

  private applicationCommentsUrl(appId: number): string {
    return `/api/applications/${appId}/comments`;
  }

  private projectCommentsUrl(projectId: number): string {
    return `/api/projects/${projectId}/comments`;
  }
}
