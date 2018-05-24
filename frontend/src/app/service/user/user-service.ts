import {Injectable} from '@angular/core';
import {UserMapper} from '../mapper/user-mapper';
import {Observable} from 'rxjs';
import {HttpUtil} from '../../util/http.util';
import {User} from '../../model/user/user';
import {HttpClient} from '@angular/common/http';
import {RoleType} from '../../model/user/role-type';
import {UserSearchCriteria} from '../../model/user/user-search-criteria';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {BackendUser} from '../backend-model/backend-user';
import {catchError, map} from 'rxjs/internal/operators';

const ACTIVE_USERS_URL = '/api/users/active';
const USERS_URL = '/api/users';
const USER_SEARCH_URL = '/api/users/search';
const USERS_BY_ROLE_URL = '/api/users/role/:roleType';
const USER_BY_USERNAME_URL = '/api/users/userName';
const CURRENT_USER_URL = '/api/users/current';

@Injectable()
export class UserService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {}

  public getActiveUsers(): Observable<Array<User>> {
    return this.http.get<BackendUser[]>(ACTIVE_USERS_URL).pipe(
      map(users => users.map(user => UserMapper.mapBackend(user))),
      catchError(err => this.errorHandler.handle(HttpUtil.extractMessage(err)))
    );
  }

  public search(searchCriteria: UserSearchCriteria): Observable<Array<User>> {
    return this.http.post<BackendUser[]>(USER_SEARCH_URL, UserMapper.mapSearchCriteria(searchCriteria)).pipe(
      map(users => users.map(user => UserMapper.mapBackend(user))),
      catchError(error => this.errorHandler.handle(error, findTranslation('user.error.search')))
    );
  }

  public getByRole(role: RoleType): Observable<Array<User>> {
    const url = USERS_BY_ROLE_URL.replace(':roleType', RoleType[role]);
    return this.http.get<BackendUser[]>(url).pipe(
      map(users => users.map(user => UserMapper.mapBackend(user))),
      catchError(err => this.errorHandler.handle(HttpUtil.extractMessage(err)))
    );
  }

  public getAllUsers(): Observable<Array<User>> {
    return this.http.get<BackendUser[]>(USERS_URL).pipe(
      map(users => users.map(user => UserMapper.mapBackend(user))),
      catchError(err => this.errorHandler.handle(HttpUtil.extractMessage(err)))
    );
  }

  public getByUsername(userName: string): Observable<User> {
    const url = USER_BY_USERNAME_URL + '/' + userName;
    return this.http.get<BackendUser>(url).pipe(
      map(user => UserMapper.mapBackend(user)),
      catchError(err => this.errorHandler.handle(HttpUtil.extractMessage(err)))
    );
  }

  public getById(id: number): Observable<User> {
    const url = `${USERS_URL}/${id}`;
    return this.http.get<BackendUser>(url).pipe(
      map(user => UserMapper.mapBackend(user)),
      catchError(err => this.errorHandler.handle(HttpUtil.extractMessage(err)))
    );
  }

  public getCurrentUser(): Observable<User> {
    return this.http.get<BackendUser>(CURRENT_USER_URL).pipe(
      map(user => UserMapper.mapBackend(user)),
      catchError(err => this.errorHandler.handle(HttpUtil.extractMessage(err)))
    );
  }

  public save(user: User): Observable<User> {
    if (user.id) {
      return this.update(user);
    } else {
      return this.create(user);
    }
  }

  public create(user: User): Observable<User> {
    return this.http.post<BackendUser>(USERS_URL, JSON.stringify(UserMapper.mapFrontend(user))).pipe(
      map(saved => UserMapper.mapBackend(saved)),
      catchError(err => this.errorHandler.handle(HttpUtil.extractMessage(err)))
    );
  }

  public update(user: User): Observable<User> {
    return this.http.put<BackendUser>(USERS_URL, JSON.stringify(UserMapper.mapFrontend(user))).pipe(
      map(saved => UserMapper.mapBackend(saved)),
      catchError(err => this.errorHandler.handle(HttpUtil.extractMessage(err)))
    );
  }
}
