import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {AuthHttp} from 'angular2-jwt/angular2-jwt';
import {SupervisionTask} from '../../model/application/supervision/supervision-task';
import {SupervisionTaskMapper} from './supervision-task-mapper';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {HttpResponse} from '../../util/http-response';
import {HttpUtil} from '../../util/http.util';
import {SupervisionWorkItem} from '../../model/application/supervision/supervision-work-item';
import {SupervisionSearchMapper} from './supervision-work-item-mapper';
import {SupervisionTaskSearchCriteria} from '../../model/application/supervision/supervision-task-search-criteria';
import {PageMapper} from '../common/page-mapper';
import {Page} from '../../model/common/page';
import {Sort} from '../../model/common/sort';
import {QueryParametersMapper} from '../mapper/query/query-parameters-mapper';
import {TimeUtil} from '../../util/time.util';
import {PageRequest} from '../../model/common/page-request';

const SUPERVISION_TASK_URL = '/api/supervisiontask';
const SUPERVISION_TASK_SEARCH_URL = '/api/supervisiontask/search';
const SUPERVISION_TASK_APP_URL = SUPERVISION_TASK_URL + '/application/:appId';
const SUPERVISION_TASK_HANDLER_URL = SUPERVISION_TASK_URL + '/handler';
const SUPERVISION_TASK_APPROVE_URL =  SUPERVISION_TASK_URL + '/:id/approve';
const SUPERVISION_TASK_REJECT_URL =  SUPERVISION_TASK_URL + '/:id/reject';

@Injectable()
export class SupervisionTaskService {

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
  }

  findTasksByApplicationId(applicationId: number): Observable<Array<SupervisionTask>> {
    const url = SUPERVISION_TASK_APP_URL.replace(':appId', String(applicationId));
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(tasks => SupervisionTaskMapper.mapBackendList(tasks))
      .catch(error => this.errorHandler.handle(error, findTranslation('supervision.task.error.fetch')));
  }

  save(task: SupervisionTask): Observable<SupervisionTask> {
    if (task.id) {
      const url = SUPERVISION_TASK_URL + '/' + task.id;
      return this.authHttp.put(url,
        JSON.stringify(SupervisionTaskMapper.mapFrontend(task)))
        .map(response => SupervisionTaskMapper.mapBackend(response.json()))
        .catch(error => this.errorHandler.handle(error, findTranslation('supervision.task.error.save')));
    } else {
      return this.authHttp.post(SUPERVISION_TASK_URL,
        JSON.stringify(SupervisionTaskMapper.mapFrontend(task)))
        .map(response => SupervisionTaskMapper.mapBackend(response.json()))
        .catch(error => this.errorHandler.handle(error, findTranslation('supervision.task.error.save')));
    }
  }

  remove(id: number): Observable<HttpResponse> {
    const url = SUPERVISION_TASK_URL + '/' + id;
    return this.authHttp.delete(url)
      .map(response => HttpUtil.extractHttpResponse(response))
      .catch(error => this.errorHandler.handle(error, findTranslation('supervisiontask.error.remove')));
  }

  search(searchCriteria: SupervisionTaskSearchCriteria, sort?: Sort, pageRequest?: PageRequest): Observable<Page<SupervisionWorkItem>> {
    return this.authHttp.post(SUPERVISION_TASK_SEARCH_URL,
      JSON.stringify(SupervisionSearchMapper.mapSearchCriteria(searchCriteria)),
      QueryParametersMapper.pageRequestToQueryParameters(pageRequest, sort))
      .map(response => PageMapper.mapBackend(response.json(), SupervisionSearchMapper.mapWorkItem))
      .catch(error => this.errorHandler.handle(error, findTranslation('supervision.task.error.fetch')));
  }

  changeHandler(handlerId: number, taskIds: Array<number>): Observable<HttpResponse> {
    const url = SUPERVISION_TASK_HANDLER_URL + '/' + handlerId;
    return this.authHttp.put(url, JSON.stringify(taskIds))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.handlerChangeFailed')));
  }

  removeHandler(taskIds: Array<number>): Observable<HttpResponse> {
    const url = SUPERVISION_TASK_HANDLER_URL + '/remove';
    return this.authHttp.put(url, JSON.stringify(taskIds))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.handlerChangeFailed')));
  }

  approve(task: SupervisionTask): Observable<SupervisionTask> {
    const url = SUPERVISION_TASK_APPROVE_URL.replace(':id', String(task.id));

    return this.authHttp.put(url, JSON.stringify(SupervisionTaskMapper.mapFrontend(task)))
      .map(response => SupervisionTaskMapper.mapBackend(response.json()))
      .catch(error => this.errorHandler.handle(error, findTranslation('supervision.task.error.approve')));
  }

  reject(task: SupervisionTask, newSupervisionDate: Date) {
    const url = SUPERVISION_TASK_REJECT_URL.replace(':id', String(task.id));
    const options = {params: {'newDate': TimeUtil.dateToBackend(newSupervisionDate)}};

    return this.authHttp.put(url, JSON.stringify(SupervisionTaskMapper.mapFrontend(task)), options)
      .map(response => SupervisionTaskMapper.mapBackend(response.json()))
      .catch(error => this.errorHandler.handle(error, findTranslation('supervision.task.error.reject')));
  }
}
