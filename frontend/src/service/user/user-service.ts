import {Injectable} from '@angular/core';
import {UserMapper} from '../mapper/user-mapper';
import {Observable} from 'rxjs/Observable';
import {ErrorUtil} from '../../util/error.util';
import {User} from '../../model/common/user';
import {UIStateHub} from '../ui-state/ui-state-hub';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';

@Injectable()
export class UserService {
  static ACTIVE_USERS_URL = '/api/users/active';
  static USERS_URL = '/api/users';
  static USER_URL = '/api/users/userName';

  constructor(private authHttp: AuthHttp, private uiState: UIStateHub) {}

  public getActiveUsers(): Observable<Array<User>> {
    return this.authHttp.get(UserService.ACTIVE_USERS_URL)
      .map(response => response.json())
      .map(users => users.map(user => UserMapper.mapBackend(user)))
      .catch(err => this.uiState.addError(ErrorUtil.extractMessage(err)));
  }

  public getAllUsers(): Observable<Array<User>> {
    return this.authHttp.get(UserService.USERS_URL)
      .map(response => response.json())
      .map(users => users.map(user => UserMapper.mapBackend(user)))
      .catch(err => this.uiState.addError(ErrorUtil.extractMessage(err)));
  }

  public getUser(userName: string): Observable<User> {
    let url = UserService.USER_URL + '/' + userName;
    return this.authHttp.get(url)
      .map(response => UserMapper.mapBackend(response.json()))
      .catch(err => this.uiState.addError(ErrorUtil.extractMessage(err)));
  }

  public save(user: User): Observable<User> {
    return this.authHttp.put(UserService.USERS_URL, JSON.stringify(UserMapper.mapFrontend(user)))
      .map(response => UserMapper.mapBackend(response.json()))
      .catch(err => this.uiState.addError(ErrorUtil.extractMessage(err)));
  }
}
