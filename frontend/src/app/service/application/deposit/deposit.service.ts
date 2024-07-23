import {ErrorHandler} from '../../error/error-handler.service';
import {Injectable} from '@angular/core';
import {Deposit} from '../../../model/application/invoice/deposit';
import {Observable} from 'rxjs';
import {DepositMapper} from './deposit-mapper';
import {findTranslation} from '../../../util/translations';
import {NumberUtil} from '../../../util/number.util';
import {BackendDeposit} from './backend-deposit';
import {HttpClient} from '@angular/common/http';
import {catchError, filter, map} from 'rxjs/internal/operators';

const DEPOSIT_URL = '/api/deposit';
const DEPOSIT_UPDATE_URL = '/api/deposit/:id';
const DEPOSIT_REMOVE_URL = '/api/deposit/:id';
const APPLICATION_DEPOSIT_URL = '/api/applications/:appId/deposit';

@Injectable()
export class DepositService {
  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  fetchByApplication(applicationId: number): Observable<Deposit> {
    const url = APPLICATION_DEPOSIT_URL.replace(':appId', String(applicationId));
    return this.http.get<BackendDeposit>(url).pipe(
      filter(deposit => !!deposit),
      map(deposit => DepositMapper.mapBackend(deposit)),
      catchError(error => this.errorHandler.handle(error, findTranslation('deposit.error.fetch')))
    );
  }

  save(deposit: Deposit): Observable<Deposit> {
    if (NumberUtil.isDefined(deposit.id)) {
      const url = DEPOSIT_UPDATE_URL.replace(':id', String(deposit.id));
      return this.http.put<BackendDeposit>(url,
        JSON.stringify(DepositMapper.mapFrontend(deposit))).pipe(
        map(saved => DepositMapper.mapBackend(saved)),
        catchError(error => this.errorHandler.handle(error, findTranslation('deposit.error.save')))
      );
    } else {
      return this.http.post<BackendDeposit>(DEPOSIT_URL,
        JSON.stringify(DepositMapper.mapFrontend(deposit))).pipe(
        map(saved => DepositMapper.mapBackend(saved)),
        catchError(error => this.errorHandler.handle(error, findTranslation('deposit.error.save')))
      );
    }
  }

  remove(id: number): Observable<object> {
    const url = DEPOSIT_REMOVE_URL.replace(':id', String(id));
    return this.http.delete(url).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('deposit.error.remove')))
    );
  }
}
