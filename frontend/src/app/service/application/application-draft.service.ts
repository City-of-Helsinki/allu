import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {Application} from '../../model/application/application';
import {ApplicationMapper} from './../mapper/application-mapper';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import '../../rxjs-extensions';
import {BackendApplication} from '../backend-model/backend-application';

const DRAFTS_URL = '/api/drafts';

@Injectable()
export class ApplicationDraftService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  /**
   * Saves given draft (new / update) and returns saved draft
   */
  public save(application: Application): Observable<Application> {
    if (application.id) {
      const url = `${DRAFTS_URL}/${application.id}`;

      return this.http.put<BackendApplication>(url,
        JSON.stringify(ApplicationMapper.mapFrontend(application)))
        .map(saved => ApplicationMapper.mapBackend(saved))
        .catch(error => this.errorHandler.handle(error, findTranslation('application.error.saveFailed')));
    } else {
      return this.http.post<BackendApplication>(DRAFTS_URL,
        JSON.stringify(ApplicationMapper.mapFrontend(application)))
        .map(saved => ApplicationMapper.mapBackend(saved))
        .catch(error => this.errorHandler.handle(error, findTranslation('application.error.saveFailed')));
    }
  }

  /**
   * Deletes given draft
   */
  public remove(id: number): Observable<{}> {
    const url = `${DRAFTS_URL}/${id}`;
    return this.http.delete<{}>(url)
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.removeFailed')));
  }

  /**
   * Converts given application draft to fully fledged application
   */
  public convertToApplication(application: Application): Observable<Application> {
    const url = `${DRAFTS_URL}/${application.id}/application`;

    return this.http.put<BackendApplication>(url,
      JSON.stringify(ApplicationMapper.mapFrontend(application)))
      .map(converted => ApplicationMapper.mapBackend(converted))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.convertToApplicationFailed')));
  }
}

