import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Observable} from 'rxjs/Observable';

import {DefaultText} from '../../model/application/cable-report/default-text';
import {HttpStatus} from '../../util/http-response';
import {HttpUtil} from '../../util/http.util';
import {ApplicationType} from '../../model/application/type/application-type';
import {DefaultTextMapper} from '../mapper/default-text-mapper';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';

const DEFAULT_TEXTS_URL = '/api/defaulttext';
const DEFAULT_TEXTS_BY_APPLICATION_TYPE_URL = DEFAULT_TEXTS_URL + '/applicationtype/:appType';

@Injectable()
export class DefaultTextService {

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
  }

  public load(applicationType: ApplicationType): Observable<Array<DefaultText>> {
    const url = DEFAULT_TEXTS_BY_APPLICATION_TYPE_URL.replace(':appType', ApplicationType[applicationType]);
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(texts => texts.map(text => DefaultTextMapper.mapBackend(text)))
      .catch(err => this.errorHandler.handle(err));
  }

  public save(text: DefaultText): Observable<DefaultText> {
    if (text.id) {
      const url = DEFAULT_TEXTS_URL + '/' + text.id;
      return this.authHttp.put(url, JSON.stringify(DefaultTextMapper.mapFrontend(text)))
        .map(response => DefaultTextMapper.mapBackend(response.json()))
        .catch(err => this.errorHandler.handle(err, findTranslation('defaultText.error.saveFailed')));
    } else {
      return this.authHttp.post(DEFAULT_TEXTS_URL, JSON.stringify(DefaultTextMapper.mapFrontend(text)))
        .map(response => DefaultTextMapper.mapBackend(response.json()))
        .catch(err => this.errorHandler.handle(err, findTranslation('defaultText.error.saveFailed')));
    }
  }

  public remove(id: number): Observable<HttpStatus> {
    const url = DEFAULT_TEXTS_URL + '/' + id;
    return this.authHttp.delete(url)
      .map(response => HttpUtil.extractHttpResponse(response))
      .catch(err => this.errorHandler.handle(err, findTranslation('defaultText.error.saveFailed')));
  }
}
