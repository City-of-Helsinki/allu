import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';

import {Comment} from '../../../model/application/comment/comment';
import {HttpUtil} from '../../../util/http.util';
import {CommentMapper} from './comment-mapper';
import {HttpResponse} from '../../../util/http-response';
import {ErrorHandler} from '../../error/error-handler.service';
import {findTranslation} from '../../../util/translations';

const COMMENTS_URL = '/api/comments';
const COMMENTS_APP_URL = COMMENTS_URL + '/applications/:appId';

@Injectable()
export class CommentService {

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {}

  getComments(applicationId: number): Observable<Array<Comment>> {
    let url = COMMENTS_APP_URL.replace(':appId', String(applicationId));
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(comments => comments.map(comment => CommentMapper.mapBackend(comment)))
      .catch(error => this.errorHandler.handle(error, findTranslation('comment.error.fetch')));
  }

  save(applicationId: number, comment: Comment): Observable<Comment> {
    if (comment.id) {
      let url = COMMENTS_URL + '/' + comment.id;
      return this.authHttp.put(url,
        JSON.stringify(CommentMapper.mapFrontend(comment)))
        .map(response => CommentMapper.mapBackend(response.json()))
        .catch(error => this.errorHandler.handle(error, findTranslation('comment.error.save')));
    } else {
      let url = COMMENTS_APP_URL.replace(':appId', String(applicationId));
      return this.authHttp.post(url,
        JSON.stringify(CommentMapper.mapFrontend(comment)))
        .map(response => CommentMapper.mapBackend(response.json()))
        .catch(error => this.errorHandler.handle(error, findTranslation('comment.error.save')));
    }
  }

  remove(id: number): Observable<HttpResponse> {
    let url = COMMENTS_URL + '/' + id;
    return this.authHttp.delete(url)
      .map(response => HttpUtil.extractHttpResponse(response))
      .catch(error => this.errorHandler.handle(error, findTranslation('comment.error.remove')));
  }
}
