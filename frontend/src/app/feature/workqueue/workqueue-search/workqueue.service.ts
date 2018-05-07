import {Injectable} from '@angular/core';
import {ApplicationSearchQuery} from '../../../model/search/ApplicationSearchQuery';
import {Observable} from 'rxjs/Observable';
import {ApplicationMapper} from '../../../service/mapper/application-mapper';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {Application} from '../../../model/application/application';
import {ApplicationQueryParametersMapper} from '../../../service/mapper/query/application-query-parameters-mapper';
import {QueryParametersMapper} from '../../../service/mapper/query/query-parameters-mapper';
import {PageMapper} from '../../../service/common/page-mapper';
import {ErrorHandler} from '../../../service/error/error-handler.service';
import {findTranslation} from '../../../util/translations';

@Injectable()
export class WorkQueueService {
  static WORK_QUEUE_URL = '/api/workqueue';

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {}

  public searchApplicationsSharedByGroup(searchQuery: ApplicationSearchQuery): Observable<Array<Application>> {
    return this.authHttp.post(
      WorkQueueService.WORK_QUEUE_URL,
      JSON.stringify(ApplicationQueryParametersMapper.mapFrontend(searchQuery)),
      QueryParametersMapper.mapSortToSearchServiceQuery(searchQuery.sort))
      .map(response => PageMapper.mapBackend(response.json(), ApplicationMapper.mapBackend))
      .map(page => page.content)
      .catch(err => this.errorHandler.handle(err, findTranslation('workqueue.error.searchFailed')));
  }
}
