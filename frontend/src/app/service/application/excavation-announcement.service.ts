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
import {RequiredTasks} from '@model/application/required-tasks';

const baseUrl = '/api/excavationannouncements';

@Injectable()
export class ExcavationAnnouncementService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {}

  reportOperationalCondition(applicationId: number, date: Date): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/operationalcondition`;
    return this.reportOfficial(url, date).pipe(
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.excavationAnnouncement.error.reportOperationalCondition')))
    );
  }

  reportWorkFinished(applicationId: number, date: Date): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/workfinished`;
    return this.reportOfficial(url, date).pipe(
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.excavationAnnouncement.error.reportWorkFinished')))
    );
  }

  reportCustomerOperationalCondition(applicationId: number, dateReport: ApplicationDateReport): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/customeroperationalcondition`;
    return this.reportCustomer(url, dateReport).pipe(
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.excavationAnnouncement.error.reportCustomerOperationalCondition')))
    );
  }

  reportCustomerWorkFinished(applicationId: number, dateReport: ApplicationDateReport): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/customerworkfinished`;
    return this.reportCustomer(url, dateReport).pipe(
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.excavationAnnouncement.error.reportCustomerWorkFinished')))
    );
  }

  reportCustomerValidity(applicationId: number, dateReport: ApplicationDateReport): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/customervalidity`;
    return this.reportCustomer(url, dateReport).pipe(
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.excavationAnnouncement.error.reportCustomerValidity')))
    );
  }

  setRequiredTasks(applicationId: number, tasks: RequiredTasks): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/requiredtasks`;
    return this.http.put<BackendApplication>(url, JSON.stringify(tasks)).pipe(
      map(response => ApplicationMapper.mapBackend(response)),
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.excavationAnnouncement.error.reportCustomerWorkFinished')))
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
