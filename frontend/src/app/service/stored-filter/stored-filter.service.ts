import {Injectable} from '@angular/core';
import {ErrorHandler} from '../error/error-handler.service';
import {Observable} from 'rxjs/Observable';
import {findTranslation} from '../../util/translations';
import {BackendStoredFilter, StoredFilterMapper} from '../mapper/stored-filter-mapper';
import {StoredFilter} from '../../model/user/stored-filter';
import {HttpUtil} from '../../util/http.util';
import {CurrentUser} from '../user/current-user';
import {HttpClient, HttpResponse} from '@angular/common/http';

const STORED_FILTER_URL = '/api/stored-filter';

@Injectable()
export class StoredFilterService {
  constructor(private http: HttpClient,
              private currentUser: CurrentUser,
              private errorHandler: ErrorHandler) {
  }

  findForCurrentUser(): Observable<StoredFilter[]> {
    return this.currentUser.user
      .map(user => `/api/users/${user.id}/stored-filter`)
      .switchMap(url => this.http.get<BackendStoredFilter[]>(url))
      .map(filters => StoredFilterMapper.mapBackendList(filters))
      .catch(error => this.errorHandler.handle(error, findTranslation('storedFilter.error.fetch')));
  }

  save(filter: StoredFilter): Observable<StoredFilter> {
    if (filter.id) {
      const url = `${STORED_FILTER_URL}/${filter.id}`;
      return this.http.put<BackendStoredFilter>(url,
        JSON.stringify(StoredFilterMapper.mapFrontend(filter)))
        .map(saved => StoredFilterMapper.mapBackend(saved))
        .catch(error => this.errorHandler.handle(error, findTranslation('storedFilter.error.create')));
    } else {
      return this.http.post<BackendStoredFilter>(STORED_FILTER_URL,
        JSON.stringify(StoredFilterMapper.mapFrontend(filter)))
        .map(saved => StoredFilterMapper.mapBackend(saved))
        .catch(error => this.errorHandler.handle(error, findTranslation('storedFilter.error.update')));
    }
  }

  remove(id: number): Observable<{}> {
    const url = `${STORED_FILTER_URL}/${id}`;
    return this.http.delete<{}>(url)
      .catch(error => this.errorHandler.handle(error, findTranslation('storedFilter.error.remove')));
  }
}
