import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';

import {ApplicationChange} from '../../model/application/application-change/application-change';
import {ApplicationChangeMapper} from '../mapper/application-change-mapper';

const HISTORY_URL = '/api/applications/:appId/history';

@Injectable()
export class HistoryService {
  constructor(private authHttp: AuthHttp) {}

  getApplicationHistory(applicationId: number): Observable<Array<ApplicationChange>> {
    const url = HISTORY_URL.replace(':appId', String(applicationId));
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(history => history.map(change => ApplicationChangeMapper.mapBackend(change)));
  }
}

