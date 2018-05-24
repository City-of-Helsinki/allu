import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {DefaultRecipient} from '../../model/common/default-recipient';
import {NumberUtil} from '../../util/number.util';
import {DefaultRecipientMapper} from '../mapper/default-recipient-mapper';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {HttpStatus} from '../../util/http-status';
import {BackendDefaultRecipient} from '../backend-model/backend-default-recipient';
import {catchError, map} from 'rxjs/internal/operators';

const DEFAULT_RECIPIENTS_URL = '/api/default-recipients';
const DEFAULT_RECIPIENTS_ID_URL = '/api/default-recipients/:id';

@Injectable()
export class DefaultRecipientService {
  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  getDefaultRecipients(): Observable<Array<DefaultRecipient>> {
    return this.http.get<BackendDefaultRecipient[]>(DEFAULT_RECIPIENTS_URL).pipe(
      map(recipients => recipients.map(r => DefaultRecipientMapper.mapBackend(r))),
      catchError(error => this.errorHandler.handle(error, findTranslation('recipient.error.fetchAll')))
    );
  }

  saveDefaultRecipient(recipient: DefaultRecipient): Observable<DefaultRecipient> {
    if (NumberUtil.isDefined(recipient.id)) {
      return this.updateRecipient(recipient);
    } else {
      return this.createRecipient(recipient);
    }
  }

  removeDefaultRecipient(id: number): Observable<{}> {
    if (NumberUtil.isDefined(id)) {
      return this.http.delete<{}>(DEFAULT_RECIPIENTS_ID_URL.replace(':id', String(id))).pipe(
        catchError(error => this.errorHandler.handle(error, findTranslation('recipient.error.remove'))));
    } else {
      return of(new HttpResponse({status: HttpStatus.OK}));
    }
  }

  private createRecipient(recipient: DefaultRecipient): Observable<DefaultRecipient> {
    return this.http.post<BackendDefaultRecipient>(DEFAULT_RECIPIENTS_URL,
      JSON.stringify(DefaultRecipientMapper.mapFrontend(recipient))).pipe(
      map(saved => DefaultRecipientMapper.mapBackend(saved)),
      catchError(error => this.errorHandler.handle(error, findTranslation('recipient.error.save')))
    );
  }

  private updateRecipient(recipient: DefaultRecipient): Observable<DefaultRecipient> {
    const url = DEFAULT_RECIPIENTS_ID_URL.replace(':id', String(recipient.id));
    return this.http.put<BackendDefaultRecipient>(url, JSON.stringify(DefaultRecipientMapper.mapFrontend(recipient))).pipe(
      map(saved => DefaultRecipientMapper.mapBackend(saved)),
      catchError(error => this.errorHandler.handle(error, findTranslation('recipient.error.save')))
    );
  }
}
