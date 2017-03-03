import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Observable} from 'rxjs/Observable';

import {DefaultText} from '../../model/application/cable-report/default-text';
import {HttpStatus} from '../../util/http-response';
import {HttpUtil} from '../../util/http.util';
import {UIStateHub} from '../ui-state/ui-state-hub';
import {ErrorInfo} from '../ui-state/error-info';
import {ErrorType} from '../ui-state/error-type';
import {ApplicationType} from '../../model/application/type/application-type';
import {DefaultTextMapper} from '../mapper/default-text-mapper';

const DEFAULT_TEXTS_URL = '/api/defaulttext';
const DEFAULT_TEXTS_BY_APPLICATION_TYPE_URL = DEFAULT_TEXTS_URL + '/applicationtype/:appType';

@Injectable()
export class DefaultTextService {

  constructor(private authHttp: AuthHttp,  private uiState: UIStateHub) {
  }

  public load(applicationType: ApplicationType): Observable<Array<DefaultText>> {
    let url = DEFAULT_TEXTS_BY_APPLICATION_TYPE_URL.replace(':appType', ApplicationType[applicationType]);
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(texts => texts.map(text => DefaultTextMapper.mapBackend(text)))
      .catch(err => this.uiState.addError(HttpUtil.extractMessage(err)));
  }

  public save(text: DefaultText): Observable<DefaultText> {
    if (text.id) {
      let url = DEFAULT_TEXTS_URL + '/' + text.id;
      return this.authHttp.put(url, JSON.stringify(DefaultTextMapper.mapFrontend(text)))
        .map(response => DefaultTextMapper.mapBackend(response.json()))
        .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.DEFAULT_TEXT_SAVE_FAILED, HttpUtil.extractMessage(err))));
    } else {
      return this.authHttp.post(DEFAULT_TEXTS_URL, JSON.stringify(DefaultTextMapper.mapFrontend(text)))
        .map(response => DefaultTextMapper.mapBackend(response.json()))
        .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.DEFAULT_TEXT_SAVE_FAILED, HttpUtil.extractMessage(err))));
    }
  }

  public remove(id: number): Observable<HttpStatus> {
    let url = DEFAULT_TEXTS_URL + '/' + id;
    return this.authHttp.delete(url)
      .map(response => HttpUtil.extractHttpResponse(response))
      .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.DEFAULT_TEXT_SAVE_FAILED, HttpUtil.extractMessage(err))));
  }
}
