import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {HttpClient} from '@angular/common/http';
import {SupervisionTask} from '../../model/application/supervision/supervision-task';
import {SupervisionTaskMapper} from './supervision-task-mapper';
import {ErrorHandler} from '../error/error-handler.service';
import {findTranslation} from '../../util/translations';
import {
  BackendSupervisionWorkItem,
  SupervisionWorkItem
} from '../../model/application/supervision/supervision-work-item';
import {SupervisionSearchMapper} from './supervision-work-item-mapper';
import {SupervisionTaskSearchCriteria} from '../../model/application/supervision/supervision-task-search-criteria';
import {PageMapper} from '../common/page-mapper';
import {Page} from '../../model/common/page';
import {Sort} from '../../model/common/sort';
import {QueryParametersMapper} from '../mapper/query/query-parameters-mapper';
import {TimeUtil} from '../../util/time.util';
import {PageRequest} from '../../model/common/page-request';
import {BackendSupervisionTask} from '../../model/application/supervision/backend-supervision-task';
import {BackendPage} from '../backend-model/backend-page';

const SUPERVISION_TASK_URL = '/api/supervisiontask';
const SUPERVISION_TASK_SEARCH_URL = '/api/supervisiontask/search';
const SUPERVISION_TASK_APP_URL = SUPERVISION_TASK_URL + '/application/:appId';
const SUPERVISION_TASK_OWNER_URL = SUPERVISION_TASK_URL + '/owner';
const SUPERVISION_TASK_APPROVE_URL =  SUPERVISION_TASK_URL + '/:id/approve';
const SUPERVISION_TASK_REJECT_URL =  SUPERVISION_TASK_URL + '/:id/reject';

@Injectable()
export class SupervisionTaskService {

  constructor(private http: HttpClient, private errorHandler: ErrorHandler) {
  }

  findTasksByApplicationId(applicationId: number): Observable<Array<SupervisionTask>> {
    const url = SUPERVISION_TASK_APP_URL.replace(':appId', String(applicationId));
    return this.http.get<BackendSupervisionTask[]>(url)
      .map(tasks => SupervisionTaskMapper.mapBackendList(tasks))
      .catch(error => this.errorHandler.handle(error, findTranslation('supervision.task.error.fetch')));
  }

  save(task: SupervisionTask): Observable<SupervisionTask> {
    if (task.id) {
      const url = SUPERVISION_TASK_URL + '/' + task.id;
      return this.http.put<BackendSupervisionTask>(url,
        JSON.stringify(SupervisionTaskMapper.mapFrontend(task)))
        .map(saved => SupervisionTaskMapper.mapBackend(saved))
        .catch(error => this.errorHandler.handle(error, findTranslation('supervision.task.error.save')));
    } else {
      return this.http.post<BackendSupervisionTask>(SUPERVISION_TASK_URL,
        JSON.stringify(SupervisionTaskMapper.mapFrontend(task)))
        .map(saved => SupervisionTaskMapper.mapBackend(saved))
        .catch(error => this.errorHandler.handle(error, findTranslation('supervision.task.error.save')));
    }
  }

  remove(id: number): Observable<{}> {
    const url = SUPERVISION_TASK_URL + '/' + id;
    return this.http.delete(url)
      .catch(error => this.errorHandler.handle(error, findTranslation('supervisiontask.error.remove')));
  }

  search(searchCriteria: SupervisionTaskSearchCriteria, sort?: Sort, pageRequest?: PageRequest): Observable<Page<SupervisionWorkItem>> {
    return this.http.post<BackendPage<BackendSupervisionWorkItem>>(SUPERVISION_TASK_SEARCH_URL,
      JSON.stringify(SupervisionSearchMapper.mapSearchCriteria(searchCriteria)),
      {params: QueryParametersMapper.mapPageRequest(pageRequest, sort)})
      .map(items => PageMapper.mapBackend(items, SupervisionSearchMapper.mapWorkItem))
      .catch(error => this.errorHandler.handle(error, findTranslation('supervision.task.error.fetch')));
  }

  changeOwner(ownerId: number, taskIds: Array<number>): Observable<{}> {
    const url = SUPERVISION_TASK_OWNER_URL + '/' + ownerId;
    return this.http.put(url, JSON.stringify(taskIds))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.handlerChangeFailed')));
  }

  removeOwner(taskIds: Array<number>): Observable<{}> {
    const url = SUPERVISION_TASK_OWNER_URL + '/remove';
    return this.http.put(url, JSON.stringify(taskIds))
      .catch(error => this.errorHandler.handle(error, findTranslation('application.error.handlerChangeFailed')));
  }

  approve(task: SupervisionTask): Observable<SupervisionTask> {
    const url = SUPERVISION_TASK_APPROVE_URL.replace(':id', String(task.id));

    return this.http.put<BackendSupervisionTask>(url, JSON.stringify(SupervisionTaskMapper.mapFrontend(task)))
      .map(approved => SupervisionTaskMapper.mapBackend(approved))
      .catch(error => this.errorHandler.handle(error, findTranslation('supervision.task.error.approve')));
  }

  reject(task: SupervisionTask, newSupervisionDate: Date) {
    const url = SUPERVISION_TASK_REJECT_URL.replace(':id', String(task.id));
    const options = {params: {'newDate': TimeUtil.dateToBackend(newSupervisionDate)}};

    return this.http.put<BackendSupervisionTask>(url, JSON.stringify(SupervisionTaskMapper.mapFrontend(task)), options)
      .map(rejected => SupervisionTaskMapper.mapBackend(rejected))
      .catch(error => this.errorHandler.handle(error, findTranslation('supervision.task.error.reject')));
  }
}
