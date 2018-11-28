import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/internal/Observable';
import {InvoicingPeriod} from '@feature/application/invoicing/invoicing-period/invoicing-period';
import {HttpClient} from '@angular/common/http';
import {ErrorHandler} from '@service/error/error-handler.service';
import {BackendInvoicingPeriod, InvoicingPeriodMapper} from '@feature/application/invoicing/invoicing-period/invoicing-period-mapper';
import {catchError, map} from 'rxjs/operators';
import {findTranslation} from '@util/translations';
import {InvoicingPeriodLength} from '@feature/application/invoicing/invoicing-period/invoicing-period-length';

const BASE_URL = '/api/applications';

@Injectable()
export class InvoicingPeriodService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  load(applicationId: number): Observable<InvoicingPeriod[]> {
    const url = `${BASE_URL}/${applicationId}/invoicingperiods`;
    return this.http.get<BackendInvoicingPeriod[]>(url).pipe(
      map(periods => InvoicingPeriodMapper.mapBackendList(periods)),
      catchError(error => this.errorHandler.handle(error, findTranslation('invoicing.period.error.fetch')))
    );
  }

  create(applicationId: number, length: InvoicingPeriodLength): Observable<InvoicingPeriod[]> {
    const url = `${BASE_URL}/${applicationId}/invoicingperiods`;
    const options = {params: {'periodLength': length.toString()}};
    return this.http.post<BackendInvoicingPeriod[]>(url, {}, options).pipe(
      map(periods => InvoicingPeriodMapper.mapBackendList(periods)),
      catchError(error => this.errorHandler.handle(error, findTranslation('invoicing.period.error.create')))
    );
  }

  update(applicationId: number, length: InvoicingPeriodLength): Observable<InvoicingPeriod[]> {
    const url = `${BASE_URL}/${applicationId}/invoicingperiods`;
    const options = {params: {'periodLength': length.toString()}};
    return this.http.put<BackendInvoicingPeriod[]>(url, {}, options).pipe(
      map(periods => InvoicingPeriodMapper.mapBackendList(periods)),
      catchError(error => this.errorHandler.handle(error, findTranslation('invoicing.period.error.update')))
    );
  }

  remove(applicationId: number): Observable<{}> {
    const url = `${BASE_URL}/${applicationId}/invoicingperiods`;
    return this.http.delete(url).pipe(
      catchError(error => this.errorHandler.handle(error, findTranslation('invoicing.period.error.remove')))
    );
  }
}
