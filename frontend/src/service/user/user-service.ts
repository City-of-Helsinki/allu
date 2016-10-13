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

  constructor(private authHttp: AuthHttp, private uiState: UIStateHub) {}

  public getActiveUsers(): Observable<Array<User>> {
    return this.authHttp.get(UserService.ACTIVE_USERS_URL)
      .map(response => response.json())
      .map(users => users.map(user => UserMapper.mapBackend(user)))
      .catch(err => this.uiState.addError(ErrorUtil.extractMessage(err)));
  }
}
