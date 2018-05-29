import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {ErrorHandler} from '../../error/error-handler.service';
import {findTranslation} from '../../../util/translations';
import {ChargeBasisEntry} from '../../../model/application/invoice/charge-basis-entry';
import {ChargeBasisEntryMapper} from '../../mapper/charge-basis-entry-mapper';
import {BackendChargeBasisEntry} from '../../backend-model/backend-charge-basis-entry';
import {catchError, map} from 'rxjs/internal/operators';

const APPLICATIONS_URL = '/api/applications';
const INVOICE_ROWS_URL = '/api/applications/:appId/charge-basis';

@Injectable()
export class InvoiceService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  saveRecipient(applicationId: number, recipientId: number): Observable<{}> {
    const url = `${APPLICATIONS_URL}/${applicationId}/invoicerecipient`;
    const params = new HttpParams().append('invoicerecipientid', String(recipientId));
    return this.http.put(url, null, {params}).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('invoice.error.invoiceRecipientSave')))
    );
  }

  getChargeBasisEntries(applicationId: number): Observable<Array<ChargeBasisEntry>> {
    const url = INVOICE_ROWS_URL.replace(':appId', String(applicationId));
    return this.http.get<BackendChargeBasisEntry[]>(url).pipe(
      map(entries => ChargeBasisEntryMapper.mapBackendArray(entries)),
      catchError(error => this.errorHandler.handle(error, findTranslation('chargeBasis.error.fetch')))
    );
  }

  saveChargeBasisEntries(applicationId: number, entries: Array<ChargeBasisEntry>): Observable<Array<ChargeBasisEntry>> {
    const url = INVOICE_ROWS_URL.replace(':appId', String(applicationId));
    return this.http.put<BackendChargeBasisEntry[]>(url, JSON.stringify(ChargeBasisEntryMapper.mapFrontendArray(entries))).pipe(
      map(saved => ChargeBasisEntryMapper.mapBackendArray(saved)),
      catchError(error => this.errorHandler.handle(error, findTranslation('chargeBasis.error.save')))
    );
  }
}
