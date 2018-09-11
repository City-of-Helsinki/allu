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

const baseUrl = '/api/excavationannouncements';

@Injectable()
export class ExcavationAnnouncementService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {}

  reportOperationalCondition(applicationId: number, dateReport: ApplicationDateReport): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/operationalcondition`;
    return this.report(url, dateReport).pipe(
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.excavationAnnouncement.error.reportOperationalCondition')))
    );
  }

  reportWorkFinished(applicationId: number, dateReport: ApplicationDateReport): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/workfinished`;
    return this.report(url, dateReport).pipe(
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.excavationAnnouncement.error.reportWorkFinished')))
    );
  }

  reportCustomerOperationalCondition(applicationId: number, dateReport: ApplicationDateReport): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/customeroperationalcondition`;
    return this.report(url, dateReport).pipe(
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.excavationAnnouncement.error.reportCustomerOperationalCondition')))
    );
  }

  reportCustomerWorkFinished(applicationId: number, dateReport: ApplicationDateReport): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/customerworkfinished`;
    return this.report(url, dateReport).pipe(
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.excavationAnnouncement.error.reportCustomerWorkFinished')))
    );
  }

  private report(url: string, dateReport: ApplicationDateReport): Observable<Application> {
    return this.http.put<BackendApplication>(url, ApplicationDateReportMapper.mapFrontend(dateReport)).pipe(
      map(response => ApplicationMapper.mapBackend(response))
    );
  }
}
