import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ErrorHandler} from '@service/error/error-handler.service';
import {Observable} from 'rxjs/index';
import {Application} from '@model/application/application';
import {ApplicationDateReport} from '@model/application/application-date-report';
import {ApplicationDateReportMapper} from '@service/mapper/application-date-report-mapper';
import {BackendApplication} from '@service/backend-model/backend-application';
import {ApplicationMapper} from '@service/mapper/application-mapper';
import {catchError, map} from 'rxjs/internal/operators';
import {findTranslation} from '@util/translations';
import {TimeUtil} from '@util/time.util';

const baseUrl = '/api/applications';

@Injectable()
export class DateReportingService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {}

  reportOperationalCondition(applicationId: number, date: Date): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/operationalcondition`;
    return this.reportOfficial(url, date).pipe(
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.error.reportOperationalCondition')))
    );
  }

  reportWorkFinished(applicationId: number, date: Date): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/workfinished`;
    return this.reportOfficial(url, date).pipe(
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.error.reportWorkFinished')))
    );
  }

  reportCustomerOperationalCondition(applicationId: number, dateReport: ApplicationDateReport): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/customeroperationalcondition`;
    return this.reportCustomer(url, dateReport).pipe(
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.error.reportCustomerOperationalCondition')))
    );
  }

  reportCustomerWorkFinished(applicationId: number, dateReport: ApplicationDateReport): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/customerworkfinished`;
    return this.reportCustomer(url, dateReport).pipe(
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.error.reportCustomerWorkFinished')))
    );
  }

  reportCustomerValidity(applicationId: number, dateReport: ApplicationDateReport): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/customervalidity`;
    return this.reportCustomer(url, dateReport).pipe(
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.error.reportCustomerValidity')))
    );
  }

  private reportCustomer(url: string, dateReport: ApplicationDateReport): Observable<Application> {
    return this.http.put<BackendApplication>(url, ApplicationDateReportMapper.mapFrontend(dateReport)).pipe(
      map(response => ApplicationMapper.mapBackend(response))
    );
  }

  private reportOfficial(url: string, date: Date): Observable<Application> {
    const body = TimeUtil.dateToBackend(date);
    return this.http.put<BackendApplication>(url, JSON.stringify(body)).pipe(
      map(response => ApplicationMapper.mapBackend(response))
    );
  }
}
