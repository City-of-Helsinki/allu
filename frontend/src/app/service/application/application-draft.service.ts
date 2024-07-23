import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Application} from '../../model/application/application';
import {ApplicationMapper} from './../mapper/application-mapper';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {BackendApplication} from '../backend-model/backend-application';
import {catchError, map} from 'rxjs/internal/operators';

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
        JSON.stringify(ApplicationMapper.mapFrontend(application))).pipe(
        map(saved => ApplicationMapper.mapBackend(saved)),
        catchError(error => this.errorHandler.handle(error, findTranslation('application.error.saveFailed')))
      );
    } else {
      return this.http.post<BackendApplication>(DRAFTS_URL,
        JSON.stringify(ApplicationMapper.mapFrontend(application))).pipe(
        map(saved => ApplicationMapper.mapBackend(saved)),
        catchError(error => this.errorHandler.handle(error, findTranslation('application.error.saveFailed')))
      );
    }
  }

  /**
   * Deletes given draft
   */
  public remove(id: number): Observable<object> {
    const url = `${DRAFTS_URL}/${id}`;
    return this.http.delete<object>(url).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.removeFailed')))
    );
  }

  /**
   * Converts given application draft to fully fledged application
   */
  public convertToApplication(application: Application): Observable<Application> {
    const url = `${DRAFTS_URL}/${application.id}/application`;

    return this.http.put<BackendApplication>(url,
      JSON.stringify(ApplicationMapper.mapFrontend(application))).pipe(
      map(converted => ApplicationMapper.mapBackend(converted)),
      catchError(error => this.errorHandler.handle(error, findTranslation('application.error.convertToApplicationFailed')))
    );
  }
}

