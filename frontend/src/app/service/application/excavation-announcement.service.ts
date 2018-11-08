import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ErrorHandler} from '@service/error/error-handler.service';
import {Observable} from 'rxjs/index';
import {Application} from '@model/application/application';
import {BackendApplication} from '@service/backend-model/backend-application';
import {ApplicationMapper} from '@service/mapper/application-mapper';
import {catchError, map} from 'rxjs/internal/operators';
import {findTranslation} from '@util/translations';
import {RequiredTasks} from '@model/application/required-tasks';

const baseUrl = '/api/excavationannouncements';

@Injectable()
export class ExcavationAnnouncementService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {}

  setRequiredTasks(applicationId: number, tasks: RequiredTasks): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/requiredtasks`;
    return this.http.put<BackendApplication>(url, JSON.stringify(tasks)).pipe(
      map(response => ApplicationMapper.mapBackend(response)),
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.excavationAnnouncement.error.reportCustomerWorkFinished')))
    );
  }
}
