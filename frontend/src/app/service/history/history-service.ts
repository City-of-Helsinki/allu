import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

import {ChangeHistoryItem} from '../../model/history/change-history-item';
import {ChangeHistoryMapper} from '../mapper/change-history-mapper';
import {BackendChangeHistoryItem} from '../backend-model/backend-change-history-item';
import {map} from 'rxjs/internal/operators';
import {ApplicationStatus} from '@model/application/application-status';

const HISTORY_URL = '/api/applications/:appId/history';
const PROJECT_URL = '/api/projects';

@Injectable()
export class HistoryService {
  constructor(private http: HttpClient) {}

  getProjectHistory(projectId: number): Observable<ChangeHistoryItem[]> {
    const url = `${PROJECT_URL}/${projectId}/history`;
    return this.getHistory(url);
  }

  getApplicationHistory(applicationId: number): Observable<ChangeHistoryItem[]> {
    const url = HISTORY_URL.replace(':appId', String(applicationId));
    return this.getHistory(url);
  }

  getStatusHistory(applicationId: number): Observable<ApplicationStatus[]> {
    const url = `/api/applications/${applicationId}/statushistory`;
    return this.http.get<ApplicationStatus[]>(url);
  }

  private getHistory(url: string): Observable<ChangeHistoryItem[]> {
    return this.http.get<BackendChangeHistoryItem[]>(url).pipe(
      map(history => history.map(change => ChangeHistoryMapper.mapBackend(change)))
    );
  }
}

