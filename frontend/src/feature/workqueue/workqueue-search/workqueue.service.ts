
import {Injectable} from '@angular/core';
import {QueryParametersMapper} from '../../../service/mapper/query-parameters-mapper';
import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';
import {Observable} from 'rxjs/Observable';
import {ApplicationMapper} from '../../../service/mapper/application-mapper';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {UIStateHub} from '../../../service/ui-state/ui-state-hub';
import {Application} from '../../../model/application/application';
import {ErrorType} from '../../../service/ui-state/error-type';
import {HttpUtil} from '../../../util/http.util.ts';
import {ErrorInfo} from '../../../service/ui-state/error-info';

@Injectable()
export class WorkQueueService {
  static WORK_QUEUE_URL = '/api/workqueue';

  constructor(private authHttp: AuthHttp, private uiState: UIStateHub) {}

  public searchApplicationsSharedByGroup(searchQuery: ApplicationSearchQuery): Observable<Array<Application>> {
    return this.authHttp.post(
      WorkQueueService.WORK_QUEUE_URL,
      JSON.stringify(QueryParametersMapper.mapFrontend(searchQuery)))
      .map(response => response.json())
      .map(json => json.map(app => ApplicationMapper.mapBackend(app)))
      .catch(err => this.uiState.addError(new ErrorInfo(ErrorType.APPLICATION_WORKQUEUE_SEARCH_FAILED, HttpUtil.extractMessage(err))));
  }
}
