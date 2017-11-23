import {Injectable} from '@angular/core';
import {AuthHttp} from 'angular2-jwt';
import {Observable} from 'rxjs/Observable';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {DefaultRecipient} from '../../model/common/default-recipient';
import {NumberUtil} from '../../util/number.util';
import {HttpResponse, HttpStatus} from '../../util/http-response';
import {HttpUtil} from '../../util/http.util';
import {DefaultRecipientMapper} from '../mapper/default-recipient-mapper';

const DEFAULT_RECIPIENTS_URL = '/api/default-recipients';
const DEFAULT_RECIPIENTS_ID_URL = '/api/default-recipients/:id';

@Injectable()
export class DefaultRecipientService {
  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
  }

  getDefaultRecipients(): Observable<Array<DefaultRecipient>> {
    return this.authHttp.get(DEFAULT_RECIPIENTS_URL)
      .map(response => response.json())
      .map(recipients => recipients.map(r => DefaultRecipientMapper.mapBackend(r)))
      .catch(error => this.errorHandler.handle(error, findTranslation('recipient.error.fetchAll')));
  }

  saveDefaultRecipient(recipient: DefaultRecipient): Observable<DefaultRecipient> {
    if (NumberUtil.isDefined(recipient.id)) {
      return this.updateRecipient(recipient);
    } else {
      return this.createRecipient(recipient);
    }
  }

  removeDefaultRecipient(id: number): Observable<HttpResponse> {
    if (NumberUtil.isDefined(id)) {
      return this.authHttp.delete(DEFAULT_RECIPIENTS_ID_URL.replace(':id', String(id)))
        .map(response => HttpUtil.extractHttpResponse(response))
        .catch(error => this.errorHandler.handle(error, findTranslation('recipient.error.remove')));
    } else {
      return Observable.of(new HttpResponse(HttpStatus.OK));
    }
  }

  private createRecipient(recipient: DefaultRecipient): Observable<DefaultRecipient> {
    return this.authHttp.post(DEFAULT_RECIPIENTS_URL, JSON.stringify(DefaultRecipientMapper.mapFrontend(recipient)))
      .map(response => DefaultRecipientMapper.mapBackend(response.json()))
      .catch(error => this.errorHandler.handle(error, findTranslation('recipient.error.save')));
  }

  private updateRecipient(recipient: DefaultRecipient): Observable<DefaultRecipient> {
    const url = DEFAULT_RECIPIENTS_ID_URL.replace(':id', String(recipient.id));
    return this.authHttp.put(url, JSON.stringify(DefaultRecipientMapper.mapFrontend(recipient)))
      .map(response => DefaultRecipientMapper.mapBackend(response.json()))
      .catch(error => this.errorHandler.handle(error, findTranslation('recipient.error.save')));
  }
}
