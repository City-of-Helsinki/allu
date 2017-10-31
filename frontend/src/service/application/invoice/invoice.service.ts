import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {ErrorHandler} from '../../error/error-handler.service';
import {findTranslation} from '../../../util/translations';
import {ChargeBasisEntry} from '../../../model/application/invoice/charge-basis-entry';
import {ChargeBasisEntryMapper} from '../../mapper/charge-basis-entry-mapper';

const INVOICE_ROWS_URL = '/api/applications/:appId/charge-basis';

@Injectable()
export class InvoiceService {

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
  }

  getChargeBasisEntries(applicationId: number): Observable<Array<ChargeBasisEntry>> {
    let url = INVOICE_ROWS_URL.replace(':appId', String(applicationId));
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(rows => ChargeBasisEntryMapper.mapBackendArray(rows))
      .catch(error => this.errorHandler.handle(error, findTranslation('chargeBasis.error.fetch')));
  }

  saveChargeBasisEntries(applicationId: number, entries: Array<ChargeBasisEntry>): Observable<Array<ChargeBasisEntry>> {
    let url = INVOICE_ROWS_URL.replace(':appId', String(applicationId));
    return this.authHttp.put(url, JSON.stringify(ChargeBasisEntryMapper.mapFrontendArray(entries)))
      .map(response => response.json())
      .map(savedRows => ChargeBasisEntryMapper.mapBackendArray(savedRows))
      .catch(error => this.errorHandler.handle(error, findTranslation('chargeBasis.error.save')));
  }
}
