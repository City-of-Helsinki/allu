import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {ExternalUser} from '../../model/common/external-user';
import {findTranslation} from '../../util/translations';
import {ErrorHandler} from '../error/error-handler.service';
import {ExternalUserMapper} from '../mapper/external-user-mapper';

const EXTERNAL_USERS_URL = '/api/externalusers';

@Injectable()
export class ExternalUserService {

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {}

  public getAllUsers(): Observable<Array<ExternalUser>> {
    return this.authHttp.get(EXTERNAL_USERS_URL)
      .map(response => response.json())
      .map(users => users.map(user => ExternalUserMapper.mapBackend(user)))
      .catch(error => this.errorHandler.handle(error, findTranslation('externalUser.error.fetchAll')));
  }

  public getUser(id: number): Observable<ExternalUser> {
    let url = EXTERNAL_USERS_URL + '/' + id;
    return this.authHttp.get(url)
      .map(response => ExternalUserMapper.mapBackend(response.json()))
      .catch(error => this.errorHandler.handle(error, findTranslation('externalUser.error.fetch')));
  }

  public save(user: ExternalUser): Observable<ExternalUser> {
    if (user.id) {
      return this.update(user);
    } else {
      return this.create(user);
    }
  }

  public create(user: ExternalUser): Observable<ExternalUser > {
    return this.authHttp.post(EXTERNAL_USERS_URL,
      JSON.stringify(ExternalUserMapper.mapFrontend(user)))
      .map(response => ExternalUserMapper.mapBackend(response.json()))
      .catch(error => this.errorHandler.handle(error, findTranslation('externalUser.error.save')));
  }

  public update(user: ExternalUser): Observable<ExternalUser> {
    return this.authHttp.put(EXTERNAL_USERS_URL,
      JSON.stringify(ExternalUserMapper.mapFrontend(user)))
      .map(response => ExternalUserMapper.mapBackend(response.json()))
      .catch(error => this.errorHandler.handle(error, findTranslation('externalUser.save.save')));
  }
}
