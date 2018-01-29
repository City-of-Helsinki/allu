import {Observable} from 'rxjs/Observable';
import {SupervisionWorkqueueState} from '../../../src/app/feature/supervision-workqueue/supervision-work-item-store';
import {WorkQueueTab} from '../../../src/app/feature/workqueue/workqueue-tab';
import {SupervisionTaskSearchCriteria} from '../../../src/app/model/application/supervision/supervision-task-search-criteria';
import {Page} from '../../../src/app/model/common/page';
import {SupervisionWorkItem} from '../../../src/app/model/application/supervision/supervision-work-item';
import {Sort} from '../../../src/app/model/common/sort';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {HttpResponse, HttpStatus} from '../../../src/app/util/http-response';
import {PageRequest} from '../../../src/app/model/common/page-request';


const initialState: SupervisionWorkqueueState = {
  tab: WorkQueueTab.OWN,
  search: new SupervisionTaskSearchCriteria(),
  page: new Page<SupervisionWorkItem>(),
  sort: new Sort(),
  selectedItems: [],
  allSelected: false
};

export class SupervisionWorkItemStoreMock {
  public changeSubject = new BehaviorSubject<SupervisionWorkqueueState>(initialState);

  get changes(): Observable<SupervisionWorkqueueState> {
    return this.changeSubject.asObservable().distinctUntilChanged();
  }

  public tabChange(tab: WorkQueueTab) {
  }

  public searchChange(search: SupervisionTaskSearchCriteria) {
  }

  public pageChange(page: Page<SupervisionWorkItem>) {
  }

  public pageRequestChange(pageRequest: PageRequest) {
  }

  public toggleAll(checked: boolean) {
  }

  public toggleSingle(taskId: number, checked: boolean) {
  }

  public changeHandlerForSelected(handlerId: number): Observable<HttpResponse> {
    return Observable.of(new HttpResponse(HttpStatus.OK));
  }

  public removeHandlerFromSelected(): Observable<HttpResponse> {
    return Observable.of(new HttpResponse(HttpStatus.OK));
  }
}
