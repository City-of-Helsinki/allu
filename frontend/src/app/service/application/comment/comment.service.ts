import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';

import {Comment} from '../../../model/application/comment/comment';
import {BackendComment, CommentMapper} from './comment-mapper';
import {ErrorHandler} from '../../error/error-handler.service';
import {findTranslation} from '../../../util/translations';

const COMMENTS_URL = '/api/comments';
const COMMENTS_APP_URL = COMMENTS_URL + '/applications/:appId';

@Injectable()
export class CommentService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {}

  getComments(applicationId: number): Observable<Array<Comment>> {
    const url = COMMENTS_APP_URL.replace(':appId', String(applicationId));
    return this.http.get<BackendComment[]>(url)
      .map(comments => comments.map(comment => CommentMapper.mapBackend(comment)))
      .catch(error => this.errorHandler.handle(error, findTranslation('comment.error.fetch')));
  }

  save(applicationId: number, comment: Comment): Observable<Comment> {
    if (comment.id) {
      const url = COMMENTS_URL + '/' + comment.id;
      return this.http.put<BackendComment>(url,
        JSON.stringify(CommentMapper.mapFrontend(comment)))
        .map(saved => CommentMapper.mapBackend(saved))
        .catch(error => this.errorHandler.handle(error, findTranslation('comment.error.save')));
    } else {
      const url = COMMENTS_APP_URL.replace(':appId', String(applicationId));
      return this.http.post<BackendComment>(url,
        JSON.stringify(CommentMapper.mapFrontend(comment)))
        .map(saved => CommentMapper.mapBackend(saved))
        .catch(error => this.errorHandler.handle(error, findTranslation('comment.error.save')));
    }
  }

  remove(id: number): Observable<{}> {
    const url = COMMENTS_URL + '/' + id;
    return this.http.delete(url)
      .catch(error => this.errorHandler.handle(error, findTranslation('comment.error.remove')));
  }
}
