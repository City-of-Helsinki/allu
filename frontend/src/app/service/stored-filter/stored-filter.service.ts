import {Injectable} from '@angular/core';
import {ErrorHandler} from '../error/error-handler.service';
import {Observable} from 'rxjs';
import {findTranslation} from '../../util/translations';
import {BackendStoredFilter, StoredFilterMapper} from '../mapper/stored-filter-mapper';
import {StoredFilter} from '../../model/user/stored-filter';
import {CurrentUser} from '../user/current-user';
import {HttpClient} from '@angular/common/http';
import {catchError, map, switchMap} from 'rxjs/internal/operators';

const STORED_FILTER_URL = '/api/stored-filter';

@Injectable()
export class StoredFilterService {
  constructor(private http: HttpClient,
              private currentUser: CurrentUser,
              private errorHandler: ErrorHandler) {
  }

  findForCurrentUser(): Observable<StoredFilter[]> {
    return this.currentUser.user.pipe(
      map(user => `/api/users/${user.id}/stored-filter`),
      switchMap(url => this.http.get<BackendStoredFilter[]>(url)),
      map(filters => StoredFilterMapper.mapBackendList(filters)),
      catchError(error => this.errorHandler.handle(error, findTranslation('storedFilter.error.fetch')))
    );
  }

  save(filter: StoredFilter): Observable<StoredFilter> {
    if (filter.id) {
      const url = `${STORED_FILTER_URL}/${filter.id}`;
      return this.http.put<BackendStoredFilter>(url,
        JSON.stringify(StoredFilterMapper.mapFrontend(filter))).pipe(
        map(saved => StoredFilterMapper.mapBackend(saved)),
        catchError(error => this.errorHandler.handle(error, findTranslation('storedFilter.error.create')))
      );
    } else {
      return this.http.post<BackendStoredFilter>(STORED_FILTER_URL,
        JSON.stringify(StoredFilterMapper.mapFrontend(filter))).pipe(
        map(saved => StoredFilterMapper.mapBackend(saved)),
        catchError(error => this.errorHandler.handle(error, findTranslation('storedFilter.error.update')))
      );
    }
  }

  remove(id: number): Observable<{}> {
    const url = `${STORED_FILTER_URL}/${id}`;
    return this.http.delete<{}>(url).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('storedFilter.error.remove')))
    );
  }
}
