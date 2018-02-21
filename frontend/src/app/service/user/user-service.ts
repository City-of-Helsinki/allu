import {Injectable} from '@angular/core';
import {UserMapper} from '../mapper/user-mapper';
import {Observable} from 'rxjs/Observable';
import {HttpUtil} from '../../util/http.util';
import {User} from '../../model/user/user';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {RoleType} from '../../model/user/role-type';
import {UserSearchCriteria} from '../../model/user/user-search-criteria';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';

const ACTIVE_USERS_URL = '/api/users/active';
const USERS_URL = '/api/users';
const USER_SEARCH_URL = '/api/users/search';
const USERS_BY_ROLE_URL = '/api/users/role/:roleType';
const USER_BY_USERNAME_URL = '/api/users/userName';
const CURRENT_USER_URL = '/api/users/current';

@Injectable()
export class UserService {

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {}

  public getActiveUsers(): Observable<Array<User>> {
    return this.authHttp.get(ACTIVE_USERS_URL)
      .map(response => response.json())
      .map(users => users.map(user => UserMapper.mapBackend(user)))
      .catch(err => this.errorHandler.handle(HttpUtil.extractMessage(err)));
  }

  public search(searchCriteria: UserSearchCriteria): Observable<Array<User>> {
    return this.authHttp.post(USER_SEARCH_URL, UserMapper.mapSearchCriteria(searchCriteria))
      .map(response => response.json())
      .map(users => users.map(user => UserMapper.mapBackend(user)))
      .catch(error => this.errorHandler.handle(error, findTranslation('user.error.search')));
  }

  public getByRole(role: RoleType): Observable<Array<User>> {
    const url = USERS_BY_ROLE_URL.replace(':roleType', RoleType[role]);
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(users => users.map(user => UserMapper.mapBackend(user)))
      .catch(err => this.errorHandler.handle(HttpUtil.extractMessage(err)));
  }

  public getAllUsers(): Observable<Array<User>> {
    return this.authHttp.get(USERS_URL)
      .map(response => response.json())
      .map(users => users.map(user => UserMapper.mapBackend(user)))
      .catch(err => this.errorHandler.handle(HttpUtil.extractMessage(err)));
  }

  public getByUsername(userName: string): Observable<User> {
    const url = USER_BY_USERNAME_URL + '/' + userName;
    return this.authHttp.get(url)
      .map(response => UserMapper.mapBackend(response.json()))
      .catch(err => this.errorHandler.handle(HttpUtil.extractMessage(err)));
  }

  public getById(id: number): Observable<User> {
    const url = `${USERS_URL}/${id}`;
    return this.authHttp.get(url)
      .map(response => UserMapper.mapBackend(response.json()))
      .catch(err => this.errorHandler.handle(HttpUtil.extractMessage(err)));
  }

  public getCurrentUser(): Observable<User> {
    return this.authHttp.get(CURRENT_USER_URL)
      .map(response => UserMapper.mapBackend(response.json()))
      .catch(err => this.errorHandler.handle(HttpUtil.extractMessage(err)));
  }

  public save(user: User): Observable<User> {
    if (user.id) {
      return this.update(user);
    } else {
      return this.create(user);
    }
  }

  public create(user: User): Observable<User> {
    return this.authHttp.post(USERS_URL, JSON.stringify(UserMapper.mapFrontend(user)))
      .map(response => UserMapper.mapBackend(response.json()))
      .catch(err => this.errorHandler.handle(HttpUtil.extractMessage(err)));
  }

  public update(user: User): Observable<User> {
    return this.authHttp.put(USERS_URL, JSON.stringify(UserMapper.mapFrontend(user)))
      .map(response => UserMapper.mapBackend(response.json()))
      .catch(err => this.errorHandler.handle(HttpUtil.extractMessage(err)));
  }
}
