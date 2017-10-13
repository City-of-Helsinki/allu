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

const SUPERVISION_TASK_URL = '/api/supervisiontask';
const SUPERVISION_TASK_SEARCH_URL = '/api/supervisiontask/search';
const SUPERVISION_TASK_APP_URL = SUPERVISION_TASK_URL + '/application/:appId';

@Injectable()
export class SupervisionTaskService {

  constructor(private authHttp: AuthHttp, private errorHandler: ErrorHandler) {
  }

  findTasksByApplicationId(applicationId: number): Observable<Array<SupervisionTask>> {
    let url = SUPERVISION_TASK_APP_URL.replace(':appId', String(applicationId));
    return this.authHttp.get(url)
      .map(response => response.json())
      .map(tasks => SupervisionTaskMapper.mapBackendList(tasks))
      .catch(error => this.errorHandler.handle(error, findTranslation('supervision.task.error.fetch')));
  }

  save(task: SupervisionTask): Observable<SupervisionTask> {
    if (task.id) {
      let url = SUPERVISION_TASK_URL + '/' + task.id;
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
    let url = SUPERVISION_TASK_URL + '/' + id;
    return this.authHttp.delete(url)
      .map(response => HttpUtil.extractHttpResponse(response))
      .catch(error => this.errorHandler.handle(error, findTranslation('supervisiontask.error.remove')));
  }

  search(searchCriteria: SupervisionTaskSearchCriteria): Observable<Array<SupervisionWorkItem>> {
    return this.authHttp.post(SUPERVISION_TASK_SEARCH_URL,
      JSON.stringify(SupervisionSearchMapper.mapSearchCriteria(searchCriteria)))
      .map(response => SupervisionSearchMapper.mapWorkItemList(response.json()))
      .catch(error => this.errorHandler.handle(error, findTranslation('supervision.task.error.fetch')));
  }
}
