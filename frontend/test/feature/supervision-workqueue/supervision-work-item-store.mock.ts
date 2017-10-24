import {Observable} from 'rxjs/Observable';
import {SupervisionWorkqueueState} from '../../../src/feature/supervision-workqueue/supervision-work-item-store';
import {WorkQueueTab} from '../../../src/feature/workqueue/workqueue-tab';
import {SupervisionTaskSearchCriteria} from '../../../src/model/application/supervision/supervision-task-search-criteria';
import {SupervisionWorkItem} from '../../../src/model/application/supervision/supervision-work-item';
import {HttpResponse, HttpStatus} from '../../../src/util/http-response';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Page} from '../../../src/model/common/page';
import {Sort} from '../../../src/model/common/sort';

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
