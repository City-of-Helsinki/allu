import {Injectable} from '@angular/core';
import {WorkQueueTab} from '../workqueue/workqueue-tab';
import {SupervisionTaskSearchCriteria} from '../../model/application/supervision/supervision-task-search-criteria';
import {SupervisionWorkItem} from '../../model/application/supervision/supervision-work-item';
import {BehaviorSubject, combineLatest, Observable} from 'rxjs';
import {SupervisionTaskService} from '../../service/supervision/supervision-task.service';
import {ArrayUtil} from '../../util/array-util';
import {Page} from '../../model/common/page';
import {Sort} from '../../model/common/sort';
import {PageRequest} from '../../model/common/page-request';
import {NotificationService} from '../notification/notification.service';
import {CurrentUser} from '../../service/user/current-user';
import {User} from '../../model/user/user';
import {debounceTime, distinctUntilChanged, map, take, tap} from 'rxjs/internal/operators';

const initialState: SupervisionWorkqueueState = {
  tab: undefined,
  search: new SupervisionTaskSearchCriteria(),
  sort: new Sort(),
  pageRequest: new PageRequest(0, 25),
  page: new Page<SupervisionWorkItem>(),
  selectedItems: [],
  allSelected: false,
  loading: false
};

@Injectable()
export class SupervisionWorkItemStore {
  private store = new BehaviorSubject<SupervisionWorkqueueState>(initialState);
  private currentUser: User;

  constructor(private service: SupervisionTaskService,
              private currentUserService: CurrentUser,
              private notification: NotificationService) {
    combineLatest(
      this.changes.pipe(map(state => state.search), distinctUntilChanged()),
      this.changes.pipe(map(state => state.sort), distinctUntilChanged()),
      this.changes.pipe(map(state => state.pageRequest), distinctUntilChanged())
    ).pipe(debounceTime(100)) // Need a small delay here so changes in multiple observables do only on refresh
     .subscribe(() => this.refresh());

    this.currentUserService.user.pipe(take(1))
      .subscribe(user => this.currentUser = user);
  }

  get changes(): Observable<SupervisionWorkqueueState> {
    return this.store.asObservable().pipe(distinctUntilChanged());
  }

  get snapshot(): SupervisionWorkqueueState {
    return {...this.store.getValue()};
  }

  public tabChange(tab: WorkQueueTab) {
    this.update({tab: tab});
  }

  public searchChange(search: SupervisionTaskSearchCriteria) {
    const pageRequest = {...this.store.getValue().pageRequest, page: 0}; // reset paging to first page
    this.update({search, pageRequest});
  }

  public sortChange(sort: Sort) {
    const pageRequest = {...this.store.getValue().pageRequest, page: 0}; // reset paging to first page
    this.update({sort, pageRequest});
  }

  public pageRequestChange(pageRequest: PageRequest) {
    this.update({pageRequest});
  }

  public pageChange(page: Page<SupervisionWorkItem>) {
    const selected = this.store.getValue().selectedItems;
    const itemIds = this.itemIds(page);
    this.update({
      page: page,
      allSelected: this.allSelected(itemIds, selected),
      loading: false
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

  public changeHandlerForSelected(handlerId: number): Observable<{}> {
    const selected = this.store.getValue().selectedItems;
    return this.service.changeOwner(handlerId, selected).pipe(
      tap(search => this.refresh())
    );
  }

  public removeHandlerFromSelected(): Observable<{}> {
    const selected = this.store.getValue().selectedItems;
    return this.service.removeOwner(selected).pipe(
      tap(search => this.refresh())
    );
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
    this.update({loading: true});

    const search = {...state.search};
    if (WorkQueueTab.OWN === state.tab) {
      search.owners = [this.currentUser.id];
    }

    this.service.search(search, state.sort, state.pageRequest)
      .subscribe(
        page => this.pageChange(page),
        err => {
          this.notification.errorInfo(err);
          this.update({loading: false});
        });
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
  loading?: boolean;
}
