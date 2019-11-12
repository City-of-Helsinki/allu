import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {ErrorHandler} from '@service/error/error-handler.service';
import {findTranslation} from '@util/translations';
import {ChargeBasisEntry} from '@model/application/invoice/charge-basis-entry';
import {ChargeBasisEntryMapper} from '@service/mapper/charge-basis-entry-mapper';
import {BackendChargeBasisEntry} from '@service/backend-model/backend-charge-basis-entry';
import {catchError, map} from 'rxjs/internal/operators';
import {NumberUtil} from '@util/number.util';
import {Invoice} from '@model/application/invoice/invoice';
import {BackendInvoice, InvoiceMapper} from '@service/mapper/invoice-mapper';
import {Customer} from '@model/customer/customer';
import {BackendCustomer} from '@service/backend-model/backend-customer';
import {CustomerMapper} from '@service/mapper/customer-mapper';

const APPLICATIONS_URL = '/api/applications';
const CHARGE_BASIS_URL = '/api/applications/:appId/charge-basis';

@Injectable()
export class InvoiceService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  getRecipient(applicationId: number): Observable<Customer> {
    const url = `${APPLICATIONS_URL}/${applicationId}/invoicerecipient`;
    return this.http.get<BackendCustomer>(url).pipe(
      map(customer => CustomerMapper.mapBackend(customer)),
      catchError(error => this.errorHandler.handle(error, findTranslation('invoice.error.invoiceRecipientFetch')))
    );
  }

  saveRecipient(applicationId: number, recipientId: number): Observable<{}> {
    const url = `${APPLICATIONS_URL}/${applicationId}/invoicerecipient`;

    const params = NumberUtil.isDefined(recipientId)
      ? new HttpParams().append('invoicerecipientid', String(recipientId))
      : new HttpParams();

    return this.http.put(url, null, {params}).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('invoice.error.invoiceRecipientSave')))
    );
  }

  getChargeBasisEntries(applicationId: number): Observable<Array<ChargeBasisEntry>> {
    const url = CHARGE_BASIS_URL.replace(':appId', String(applicationId));
    return this.http.get<BackendChargeBasisEntry[]>(url).pipe(
      map(entries => ChargeBasisEntryMapper.mapBackendArray(entries)),
      catchError(error => this.errorHandler.handle(error, findTranslation('chargeBasis.error.fetch')))
    );
  }

  saveChargeBasisEntries(applicationId: number, entries: Array<ChargeBasisEntry>): Observable<Array<ChargeBasisEntry>> {
    const url = CHARGE_BASIS_URL.replace(':appId', String(applicationId));
    return this.http.put<BackendChargeBasisEntry[]>(url, JSON.stringify(ChargeBasisEntryMapper.mapFrontendArray(entries))).pipe(
      map(saved => ChargeBasisEntryMapper.mapBackendArray(saved)),
      catchError(error => this.errorHandler.handle(error, findTranslation('chargeBasis.error.save')))
    );
  }

  setInvoicable(applicationId: number, entryId: number, invoicable: boolean): Observable<ChargeBasisEntry> {
    const url = `${APPLICATIONS_URL}/${applicationId}/charge-basis/${entryId}/invoicable`;
    const params = new HttpParams().append('invoicable', String(invoicable));

    return this.http.put<BackendChargeBasisEntry>(url, undefined, {params}).pipe(
      map(updated => ChargeBasisEntryMapper.mapBackend(updated)),
      catchError(error => this.errorHandler.handle(error, findTranslation('chargeBasis.error.setInvoicable')))
    );
  }

  getInvoices(applicationId: number): Observable<Invoice[]> {
    const url = `${APPLICATIONS_URL}/${applicationId}/invoices`;
    return this.http.get<BackendInvoice[]>(url).pipe(
      map(invoices => InvoiceMapper.mapBackendInvoices(invoices)),
      catchError(error => this.errorHandler.handle(error, findTranslation('invoice.error.fetch')))
    );
  }
}
