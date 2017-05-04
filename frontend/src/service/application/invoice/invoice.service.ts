import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {HttpUtil} from '../../../util/http.util';
import {HttpResponse} from '../../../util/http-response';
import {ErrorHandler} from '../../error/error-handler.service';
import {findTranslation} from '../../../util/translations';
import {InvoiceRow} from '../../../model/application/invoice/invoice-row';
import {InvoiceRowMapper} from '../../mapper/invoice-row-mapper';

const INVOICE_ROWS_URL = '/api/applications/:appId/invoice-rows';

@Injectable()
export class InvoiceService {

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
  }

  getInvoiceRows(applicationId: number): Observable<Array<InvoiceRow>> {
    let url = INVOICE_ROWS_URL.replace(':appId', String(applicationId));
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(rows => InvoiceRowMapper.mapBackendArray(rows))
      .catch(error => this.errorHandler.handle(error, findTranslation('invoice.row.error.fetch')));
  }

  saveInvoiceRows(applicationId: number, rows: Array<InvoiceRow>): Observable<Array<InvoiceRow>> {
    let url = INVOICE_ROWS_URL.replace(':appId', String(applicationId));
    return this.authHttp.put(url, JSON.stringify(InvoiceRowMapper.mapFrontendArray(rows)))
      .map(response => response.json())
      .map(savedRows => InvoiceRowMapper.mapBackendArray(savedRows))
      .catch(error => this.errorHandler.handle(error, findTranslation('invoice.row.error.save')));
  }
}
