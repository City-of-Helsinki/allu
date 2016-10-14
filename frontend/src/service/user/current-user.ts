import {JwtHelper} from 'angular2-jwt/angular2-jwt';
import {Some} from '../../util/option';
import {Option} from '../../util/option';

export class CurrentUser {
  static userName(): Option<string> {
    return this.decode().map(token => token['sub']);
  }

  static roles(): Option<Array<string>> {
    return this.decode().map(token => token['alluRoles']);
  }

  private static decode(): Option<Object> {
    return Some(localStorage['jwt']).map(t => new JwtHelper().decodeToken(t));
  }
}
