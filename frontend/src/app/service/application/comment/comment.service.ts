import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

import {Comment} from '../../../model/application/comment/comment';
import {BackendComment, CommentMapper} from './comment-mapper';
import {ErrorHandler} from '../../error/error-handler.service';
import {findTranslation} from '../../../util/translations';
import {NumberUtil} from '../../../util/number.util';
import {ActionTargetType} from '../../../feature/allu/actions/action-target-type';
import {catchError, map} from 'rxjs/internal/operators';

const COMMENTS_URL = '/api/comments';

@Injectable()
export class CommentService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {}

  getCommentsFor(target: ActionTargetType, id: number): Observable<Array<Comment>> {
    const url = this.urlByTarget(target, id);
    return this.getComments(url);
  }

  insertFor(target: ActionTargetType, id: number, comment: Comment): Observable<Comment> {
    const url = this.urlByTarget(target, id);
    return this.insertComment(url, comment);
  }

  saveComment(target: ActionTargetType, id: number, comment: Comment): Observable<Comment> {
    return NumberUtil.isDefined(comment.id)
      ? this.updateComment(comment)
      : this.insertFor(target, id, comment);
  }

  remove(id: number): Observable<{}> {
    const url = `${COMMENTS_URL}/${id}`;
    return this.http.delete(url).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('comment.error.remove')))
    );
  }

  private getComments(url: string): Observable<Comment[]> {
    return this.http.get<BackendComment[]>(url).pipe(
      map(comments => comments.map(comment => CommentMapper.mapBackend(comment))),
      catchError(error => this.errorHandler.handle(error, findTranslation('comment.error.fetch')))
    );
  }

  private insertComment(url: string, comment: Comment): Observable<Comment> {
    return this.http.post<BackendComment>(url,
      JSON.stringify(CommentMapper.mapFrontend(comment))).pipe(
      map(saved => CommentMapper.mapBackend(saved)),
      catchError(error => this.errorHandler.handle(error, findTranslation('comment.error.save')))
    );
  }

  private updateComment(comment: Comment): Observable<Comment> {
    const url = `${COMMENTS_URL}/${comment.id}`;
    return this.http.put<BackendComment>(url,
      JSON.stringify(CommentMapper.mapFrontend(comment))).pipe(
      map(saved => CommentMapper.mapBackend(saved)),
      catchError(error => this.errorHandler.handle(error, findTranslation('comment.error.save')))
    );
  }

  private urlByTarget(target: ActionTargetType, id: number): string {
    return target === ActionTargetType.Application
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
