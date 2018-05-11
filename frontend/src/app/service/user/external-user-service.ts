import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {ExternalUser} from '../../model/common/external-user';
import {findTranslation} from '../../util/translations';
import {ErrorHandler} from '../error/error-handler.service';
import {ExternalUserMapper} from '../mapper/external-user-mapper';
import {BackendExternalUser} from '../backend-model/backend-external-user';

const EXTERNAL_USERS_URL = '/api/externalusers';

@Injectable()
export class ExternalUserService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {}

  public getAllUsers(): Observable<Array<ExternalUser>> {
    return this.http.get<BackendExternalUser[]>(EXTERNAL_USERS_URL)
      .map(users => users.map(user => ExternalUserMapper.mapBackend(user)))
      .catch(error => this.errorHandler.handle(error, findTranslation('externalUser.error.fetchAll')));
  }

  public getUser(id: number): Observable<ExternalUser> {
    const url = EXTERNAL_USERS_URL + '/' + id;
    return this.http.get<BackendExternalUser>(url)
      .map(users => ExternalUserMapper.mapBackend(users))
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
    return this.http.post<BackendExternalUser>(EXTERNAL_USERS_URL,
      JSON.stringify(ExternalUserMapper.mapFrontend(user)))
      .map(saved => ExternalUserMapper.mapBackend(saved))
      .catch(error => this.errorHandler.handle(error, findTranslation('externalUser.error.save')));
  }

  public update(user: ExternalUser): Observable<ExternalUser> {
    return this.http.put<BackendExternalUser>(EXTERNAL_USERS_URL,
      JSON.stringify(ExternalUserMapper.mapFrontend(user)))
      .map(saved => ExternalUserMapper.mapBackend(saved))
      .catch(error => this.errorHandler.handle(error, findTranslation('externalUser.save.save')));
  }
}
