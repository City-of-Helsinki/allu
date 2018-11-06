import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ErrorHandler} from '@service/error/error-handler.service';
import {Observable} from 'rxjs/index';
import {Application} from '@model/application/application';
import {BackendApplication} from '@service/backend-model/backend-application';
import {ApplicationMapper} from '@service/mapper/application-mapper';
import {catchError, map} from 'rxjs/internal/operators';
import {findTranslation} from '@util/translations';
import {TimeUtil} from '@util/time.util';

const baseUrl = '/api/arearentals';

@Injectable()
export class AreaRentalService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {}

  reportWorkFinished(applicationId: number, date: Date): Observable<Application> {
    const url = `${baseUrl}/${applicationId}/workfinished`;
    return this.reportOfficial(url, date).pipe(
      catchError(error =>
        this.errorHandler.handle(error, findTranslation('application.areaRental.error.reportWorkFinished')))
    );
  }

  private reportOfficial(url: string, date: Date): Observable<Application> {
    const body = TimeUtil.dateToBackend(date);
    return this.http.put<BackendApplication>(url, JSON.stringify(body)).pipe(
      map(response => ApplicationMapper.mapBackend(response))
    );
  }
}
