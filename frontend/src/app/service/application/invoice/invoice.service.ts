import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {ErrorHandler} from '../../error/error-handler.service';
import {findTranslation} from '../../../util/translations';
import {ChargeBasisEntry} from '../../../model/application/invoice/charge-basis-entry';
import {ChargeBasisEntryMapper} from '../../mapper/charge-basis-entry-mapper';
import {BackendChargeBasisEntry} from '../../backend-model/backend-charge-basis-entry';

const INVOICE_ROWS_URL = '/api/applications/:appId/charge-basis';

@Injectable()
export class InvoiceService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  getChargeBasisEntries(applicationId: number): Observable<Array<ChargeBasisEntry>> {
    const url = INVOICE_ROWS_URL.replace(':appId', String(applicationId));
    return this.http.get<BackendChargeBasisEntry[]>(url)
      .map(entries => ChargeBasisEntryMapper.mapBackendArray(entries))
      .catch(error => this.errorHandler.handle(error, findTranslation('chargeBasis.error.fetch')));
  }

  saveChargeBasisEntries(applicationId: number, entries: Array<ChargeBasisEntry>): Observable<Array<ChargeBasisEntry>> {
    const url = INVOICE_ROWS_URL.replace(':appId', String(applicationId));
    return this.http.put<BackendChargeBasisEntry[]>(url, JSON.stringify(ChargeBasisEntryMapper.mapFrontendArray(entries)))
      .map(saved => ChargeBasisEntryMapper.mapBackendArray(saved))
      .catch(error => this.errorHandler.handle(error, findTranslation('chargeBasis.error.save')));
  }
}
