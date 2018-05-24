import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

import {ApplicationChange} from '../../model/application/application-change/application-change';
import {ApplicationChangeMapper} from '../mapper/application-change-mapper';
import {BackendApplicationChange} from '../backend-model/backend-application-change';
import {map} from 'rxjs/internal/operators';

const HISTORY_URL = '/api/applications/:appId/history';

@Injectable()
export class HistoryService {
  constructor(private http: HttpClient) {}

  getApplicationHistory(applicationId: number): Observable<Array<ApplicationChange>> {
    const url = HISTORY_URL.replace(':appId', String(applicationId));
    return this.http.get<BackendApplicationChange[]>(url).pipe(
      map(history => history.map(change => ApplicationChangeMapper.mapBackend(change)))
    );
  }
}

