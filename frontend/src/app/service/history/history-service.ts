import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

import {ChangeHistoryItem} from '../../model/history/change-history-item';
import {ChangeHistoryMapper} from '../mapper/change-history-mapper';
import {BackendChangeHistoryItem} from '../backend-model/backend-change-history-item';
import {map} from 'rxjs/internal/operators';

const HISTORY_URL = '/api/applications/:appId/history';

@Injectable()
export class HistoryService {
  constructor(private http: HttpClient) {}

  getApplicationHistory(applicationId: number): Observable<Array<ChangeHistoryItem>> {
    const url = HISTORY_URL.replace(':appId', String(applicationId));
    return this.http.get<BackendChangeHistoryItem[]>(url).pipe(
      map(history => history.map(change => ChangeHistoryMapper.mapBackend(change)))
    );
  }
}

