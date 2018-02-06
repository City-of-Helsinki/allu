import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {Application} from '../../model/application/application';
import {ApplicationMapper} from './../mapper/application-mapper';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {HttpResponse} from '../../util/http-response';
import {HttpUtil} from '../../util/http.util';
import '../../rxjs-extensions';

const DRAFTS_URL = '/api/drafts';

@Injectable()
export class ApplicationDraftService {

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
  }

  /**
   * Saves given draft (new / update) and returns saved draft
   */
  public save(application: Application): Observable<Application> {
    if (application.id) {
      const url = `${DRAFTS_URL}/${application.id}`;

      return this.authHttp.put(url,
        JSON.stringify(ApplicationMapper.mapFrontend(application)))
        .map(response => ApplicationMapper.mapBackend(response.json()))
        .catch(error => this.errorHandler.handle(error, findTranslation('application.error.saveFailed')));
    } else {
      return this.authHttp.post(DRAFTS_URL,
        JSON.stringify(ApplicationMapper.mapFrontend(application)))
        .map(response => ApplicationMapper.mapBackend(response.json()))
        .catch(error => this.errorHandler.handle(error, findTranslation('application.error.saveFailed')));
    }
  }

  /**
   * Deletes given draft
   */
  public remove(id: number): Observable<HttpResponse> {
    const url = `${DRAFTS_URL}/${id}`;
    return this.authHttp.delete(url)
      .map(response => HttpUtil.extractHttpResponse(response))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.removeFailed')));
  }

  /**
   * Converts given application draft to fully fledged application
   */
  public convertToApplication(application: Application): Observable<Application> {
    const url = `${DRAFTS_URL}/${application.id}/application`;

    return this.authHttp.put(url,
      JSON.stringify(ApplicationMapper.mapFrontend(application)))
      .map(response => ApplicationMapper.mapBackend(response.json()))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.convertToApplicationFailed')));
  }
}

