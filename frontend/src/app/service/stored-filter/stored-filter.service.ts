import {Injectable} from '@angular/core';
import {ErrorHandler} from '../error/error-handler.service';
import {AuthHttp} from 'angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {HttpResponse} from '../../util/http-response';
import {findTranslation} from '../../util/translations';
import {StoredFilterMapper} from '../mapper/stored-filter-mapper';
import {StoredFilter} from '../../model/user/stored-filter';
import {HttpUtil} from '../../util/http.util';
import {CurrentUser} from '../user/current-user';

const STORED_FILTER_URL = '/api/stored-filter';

@Injectable()
export class StoredFilterService {
  constructor(private authHttp: AuthHttp,
              private currentUser: CurrentUser,
              private errorHandler: ErrorHandler) {
  }

  findForCurrentUser(): Observable<StoredFilter[]> {
    return this.currentUser.user
      .map(user => `/api/users/${user.id}/stored-filter`)
      .switchMap(url => this.authHttp.get(url))
      .map(response => response.json())
      .map(tasks => StoredFilterMapper.mapBackendList(tasks))
      .catch(error => this.errorHandler.handle(error, findTranslation('storedFilter.error.fetch')));
  }

  save(filter: StoredFilter): Observable<StoredFilter> {
    if (filter.id) {
      const url = `${STORED_FILTER_URL}/${filter.id}`;
      return this.authHttp.put(url,
        JSON.stringify(StoredFilterMapper.mapFrontend(filter)))
        .map(response => StoredFilterMapper.mapBackend(response.json()))
        .catch(error => this.errorHandler.handle(error, findTranslation('storedFilter.error.create')));
    } else {
      return this.authHttp.post(STORED_FILTER_URL,
        JSON.stringify(StoredFilterMapper.mapFrontend(filter)))
        .map(response => StoredFilterMapper.mapBackend(response.json()))
        .catch(error => this.errorHandler.handle(error, findTranslation('storedFilter.error.update')));
    }
  }

  remove(id: number): Observable<HttpResponse> {
    const url = `${STORED_FILTER_URL}/${id}`;
    return this.authHttp.delete(url)
      .map(response => HttpUtil.extractHttpResponse(response))
      .catch(error => this.errorHandler.handle(error, findTranslation('storedFilter.error.remove')));
  }

  setAsDefault(id: number): Observable<HttpResponse> {
    const url = `${STORED_FILTER_URL}/${id}/set-default`;
    return this.authHttp.put(url, '')
      .map(response => HttpUtil.extractHttpResponse(response))
      .catch(error => this.errorHandler.handle(error, findTranslation('storedFilter.error.setDefault')));
  }
}
