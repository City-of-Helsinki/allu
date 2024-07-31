import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

import {DefaultText} from '../../model/application/cable-report/default-text';
import {ApplicationType} from '../../model/application/type/application-type';
import {DefaultTextMapper} from '../mapper/default-text-mapper';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {BackendDefaultText} from '../backend-model/backend-default-text';
import {catchError, map} from 'rxjs/internal/operators';

const DEFAULT_TEXTS_URL = '/api/defaulttext';
const DEFAULT_TEXTS_BY_APPLICATION_TYPE_URL = DEFAULT_TEXTS_URL + '/applicationtype/:appType';

@Injectable()
export class DefaultTextService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  public load(applicationType: ApplicationType): Observable<Array<DefaultText>> {
    const url = DEFAULT_TEXTS_BY_APPLICATION_TYPE_URL.replace(':appType', ApplicationType[applicationType]);
    return this.http.get<BackendDefaultText[]>(url).pipe(
      map(texts => texts.map(text => DefaultTextMapper.mapBackend(text))),
      catchError(err => this.errorHandler.handle(err))
    );
  }

  public save(text: DefaultText): Observable<DefaultText> {
    if (text.id) {
      const url = DEFAULT_TEXTS_URL + '/' + text.id;
      return this.http.put<BackendDefaultText>(url, JSON.stringify(DefaultTextMapper.mapFrontend(text))).pipe(
        map(saved => DefaultTextMapper.mapBackend(saved)),
        catchError(err => this.errorHandler.handle(err, findTranslation('defaultText.error.saveFailed')))
        );
    } else {
      return this.http.post<BackendDefaultText>(DEFAULT_TEXTS_URL, JSON.stringify(DefaultTextMapper.mapFrontend(text))).pipe(
        map(saved => DefaultTextMapper.mapBackend(saved)),
        catchError(err => this.errorHandler.handle(err, findTranslation('defaultText.error.saveFailed')))
      );
    }
  }

  public remove(id: number): Observable<{}> {
    const url = DEFAULT_TEXTS_URL + '/' + id;
    return this.http.delete(url).pipe(
      catchError(err => this.errorHandler.handle(err, findTranslation('defaultText.error.saveFailed')))
    );
  }
}
