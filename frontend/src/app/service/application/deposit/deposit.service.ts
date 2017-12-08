import {AuthHttp} from 'angular2-jwt';
import {ErrorHandler} from '../../error/error-handler.service';
import {Injectable} from '@angular/core';
import {Deposit} from '../../../model/application/invoice/deposit';
import {Observable} from 'rxjs/Observable';
import {HttpResponse} from '../../../util/http-response';
import {DepositMapper} from './deposit-mapper';
import {findTranslation} from '../../../util/translations';
import {HttpUtil} from '../../../util/http.util';
import {NumberUtil} from '../../../util/number.util';
import {StringUtil} from '../../../util/string.util';

const DEPOSIT_URL = '/api/deposit';
const DEPOSIT_UPDATE_URL = '/api/deposit/:id';
const DEPOSIT_REMOVE_URL = '/api/deposit/:id';
const APPLICATION_DEPOSIT_URL = '/api/applications/:appId/deposit';

@Injectable()
export class DepositService {
  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
  }

  fetchByApplication(applicationId: number): Observable<Deposit> {
    const url = APPLICATION_DEPOSIT_URL.replace(':appId', String(applicationId));
    return this.authHttp.get(url)
      .filter(response => !StringUtil.isEmpty(response.text()))
      .map(response => response.json())
      .map(deposit => DepositMapper.mapBackend(deposit))
      .catch(error => this.errorHandler.handle(error, findTranslation('deposit.error.fetch')));
  }

  save(deposit: Deposit): Observable<Deposit> {
    if (NumberUtil.isDefined(deposit.id)) {
      const url = DEPOSIT_UPDATE_URL.replace(':id', String(deposit.id));
      return this.authHttp.put(url,
        JSON.stringify(DepositMapper.mapFrontend(deposit)))
        .map(response => DepositMapper.mapBackend(response.json()))
        .catch(error => this.errorHandler.handle(error, findTranslation('deposit.error.save')));
    } else {
      return this.authHttp.post(DEPOSIT_URL,
        JSON.stringify(DepositMapper.mapFrontend(deposit)))
        .map(response => DepositMapper.mapBackend(response.json()))
        .catch(error => this.errorHandler.handle(error, findTranslation('deposit.error.save')));
    }
  }

  remove(id: number): Observable<HttpResponse> {
    const url = DEPOSIT_REMOVE_URL.replace(':id', String(id));
    return this.authHttp.delete(url)
      .map(response => HttpUtil.extractHttpResponse(response))
      .catch(error => this.errorHandler.handle(error, findTranslation('deposit.error.remove')));
  }
}
