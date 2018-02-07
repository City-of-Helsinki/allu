import {Injectable} from '@angular/core';
import {WorkQueueTab} from '../workqueue/workqueue-tab';
import {SupervisionTaskSearchCriteria} from '../../model/application/supervision/supervision-task-search-criteria';
import {SupervisionWorkItem} from '../../model/application/supervision/supervision-work-item';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';
import {SupervisionTaskService} from '../../service/supervision/supervision-task.service';
import {HttpResponse} from '../../util/http-response';
import {ArrayUtil} from '../../util/array-util';
import {Page} from '../../model/common/page';
import {Sort} from '../../model/common/sort';
import {PageRequest} from '../../model/common/page-request';

const initialState: SupervisionWorkqueueState = {
  tab: undefined,
  search: new SupervisionTaskSearchCriteria(),
  sort: new Sort(),
  pageRequest: new PageRequest(),
  page: new Page<SupervisionWorkItem>(),
  selectedItems: [],
  allSelected: false
};

@Injectable()
export class SupervisionWorkItemStore {
  private store = new BehaviorSubject<SupervisionWorkqueueState>(initialState);

  constructor(private service: SupervisionTaskService) {
    Observable.combineLatest(
      this.changes.map(state => state.search).distinctUntilChanged(),
      this.changes.map(state => state.sort).distinctUntilChanged(),
      this.changes.map(state => state.pageRequest).distinctUntilChanged()
    ).subscribe(() => this.refresh());
  }

  get changes(): Observable<SupervisionWorkqueueState> {
    return this.store.asObservable().distinctUntilChanged();
  }

  public tabChange(tab: WorkQueueTab) {
    this.update({tab: tab});
  }

  public searchChange(search: SupervisionTaskSearchCriteria) {
    this.update({search: search});
  }

  public sortChange(sort: Sort) {
    this.update({sort: sort});
  }

  public pageRequestChange(pageRequest: PageRequest) {
    this.update({pageRequest});
  }

  public pageChange(page: Page<SupervisionWorkItem>) {
    const selected = this.store.getValue().selectedItems;
    const itemIds = this.itemIds(page);
    this.update({
      page: page,
      allSelected: this.allSelected(itemIds, selected)
    });
  }

  public toggleAll(checked: boolean) {
    if (checked) {
      const itemIds = this.itemIds(this.store.getValue().page);
      this.selectedItems(itemIds);
    } else {
      this.selectedItems([]);
    }
  }

  public toggleSingle(taskId: number, checked: boolean) {
    const current = this.store.getValue().selectedItems;
    const selected = checked
      ? current.concat(taskId)
      : current.filter(id => id !== taskId);
    this.selectedItems(selected);
  }

  public changeHandlerForSelected(handlerId: number): Observable<HttpResponse> {
    const selected = this.store.getValue().selectedItems;
    return this.service.changeHandler(handlerId, selected)
      .do(search => this.refresh());
  }

  public removeHandlerFromSelected(): Observable<HttpResponse> {
    const selected = this.store.getValue().selectedItems;
    return this.service.removeHandler(selected)
      .do(search => this.refresh());
  }

  private selectedItems(selected: Array<number>) {
    const itemIds = this.itemIds(this.store.getValue().page);
    this.update({
      selectedItems: selected,
      allSelected: this.allSelected(itemIds, selected)
    });
  }

  private update(newState: SupervisionWorkqueueState) {
    const oldState = this.store.getValue();
    this.store.next({...oldState, ...newState});
  }

  private refresh(): void {
    const state = this.store.getValue();
    this.selectedItems([]);
    this.service.search(state.search, state.sort, state.pageRequest)
      .subscribe(page => this.pageChange(page));
  }

  private allSelected(items: Array<number>, selected: Array<number>): boolean {
    const hasItems = items.length > 0;
    return hasItems && ArrayUtil.containSame(items, selected);
  }

  private itemIds(page: Page<SupervisionWorkItem>): Array<number> {
    return page.content.map(item => item.id);
  }
}

export interface SupervisionWorkqueueState {
  tab?: WorkQueueTab;
  search?: SupervisionTaskSearchCriteria;
  sort?: Sort;
  pageRequest?: PageRequest;
  page?: Page<SupervisionWorkItem>;
  selectedItems?: Array<number>;
  allSelected?: boolean;
}
