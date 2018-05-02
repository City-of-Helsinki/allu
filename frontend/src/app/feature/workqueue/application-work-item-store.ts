import {Injectable} from '@angular/core';
import {WorkQueueTab} from '../workqueue/workqueue-tab';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';
import {ArrayUtil} from '../../util/array-util';
import {Page} from '../../model/common/page';
import {Sort} from '../../model/common/sort';
import {PageRequest} from '../../model/common/page-request';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {Application} from '../../model/application/application';
import '../../rxjs-extensions';
import {ApplicationService} from '../../service/application/application.service';
import {NotificationService} from '../../service/notification/notification.service';
import {CurrentUser} from '../../service/user/current-user';
import {User} from '../../model/user/user';
import {ObjectUtil} from '../../util/object.util';

const initialState: ApplicationWorkqueueState = {
  tab: undefined,
  search: new ApplicationSearchQuery(),
  sort: new Sort(),
  pageRequest: new PageRequest(0, 25),
  page: new Page<Application>(),
  selectedItems: [],
  allSelected: false,
  loading: false
};

@Injectable()
export class ApplicationWorkItemStore {
  private store = new BehaviorSubject<ApplicationWorkqueueState>(initialState);
  private currentUser: User;

  constructor(private service: ApplicationService, private currentUserService: CurrentUser) {
    Observable.combineLatest(
      this.changes.map(state => state.search).distinctUntilChanged(),
      this.changes.map(state => state.sort).distinctUntilChanged(),
      this.changes.map(state => state.pageRequest).distinctUntilChanged()
    ).debounceTime(100) // Need a small delay here so changes in multiple observables do only on refresh
      .switchMap(() => this.pagedSearch())
      .subscribe(page => this.pageChange(page));

    this.currentUserService.user.take(1)
      .subscribe(user => this.currentUser = user);
  }

  get snapshot(): ApplicationWorkqueueState {
    return this.store.getValue();
  }

  get changes(): Observable<ApplicationWorkqueueState> {
    return this.store.asObservable().distinctUntilChanged();
  }

  public change(state: ApplicationWorkqueueState): void {
    this.store.next({...this.snapshot, ...state});
  }

  public tabChange(tab: WorkQueueTab) {
    this.store.next({ ...this.store.getValue(), tab });
  }

  public searchChange(search: ApplicationSearchQuery) {
    const pageRequest = {...this.store.getValue().pageRequest, page: 0}; // reset paging to first page
    this.store.next({ ...this.store.getValue(), search, pageRequest });
  }

  public sortChange(sort: Sort) {
    const pageRequest = {...this.store.getValue().pageRequest, page: 0}; // reset paging to first page
    this.store.next({ ...this.store.getValue(), sort, pageRequest });
  }

  public pageRequestChange(pageRequest: PageRequest) {
    this.store.next({
      ...this.store.getValue(),
      pageRequest
    });
  }

  public pageChange(page: Page<Application>) {
    const selected = this.store.getValue().selectedItems;
    const itemIds = this.itemIds(page);
    this.store.next({
      ...this.store.getValue(),
      page: page,
      allSelected: this.allSelected(itemIds, selected),
      loading: false
    });
  }

  public toggleAll(checked: boolean) {
    if (checked) {
      const itemIds = this.itemIds(this.store.getValue().page);
      this.selectedItemsChange(itemIds);
    } else {
      this.selectedItemsChange([]);
    }
  }

  public toggleSingle(taskId: number, checked: boolean) {
    const current = this.store.getValue().selectedItems;
    const selected = checked
      ? current.concat(taskId)
      : current.filter(id => id !== taskId);
    this.selectedItemsChange(selected);
  }

  public changeOwnerForSelected(ownerId: number): Observable<Page<Application>> {
    const selected = this.store.getValue().selectedItems;
    return this.service.changeOwner(ownerId, selected)
      .switchMap(() => this.pagedSearch())
      .do(result => this.pageChange(result));
  }

  public removeOwnerFromSelected(): Observable<Page<Application>> {
    const selected = this.store.getValue().selectedItems;
    return this.service.removeOwner(selected)
      .switchMap(() => this.pagedSearch())
      .do(result => this.pageChange(result));
  }

  public selectedItemsChange(selected: Array<number>) {
    const itemIds = this.itemIds(this.store.getValue().page);
    this.store.next({
      ...this.store.getValue(),
      selectedItems: selected,
      allSelected: this.allSelected(itemIds, selected)
    });
  }

  private pagedSearch(): Observable<Page<Application>> {
    this.store.next({...this.snapshot, loading: true});
    const state = this.snapshot;

    const search = ObjectUtil.clone(state.search);
    if (WorkQueueTab.OWN === state.tab) {
      search.owner = [this.currentUser.userName];
    }

    return this.service.pagedSearch(search, state.sort, state.pageRequest)
      .catch(err => NotificationService.errorCatch(err, new Page<Application>()));
  }

  private allSelected(items: Array<number>, selected: Array<number>): boolean {
    const hasItems = items.length > 0;
    return hasItems && ArrayUtil.containSame(items, selected);
  }

  private itemIds(page: Page<Application>): Array<number> {
    return page.content.map(item => item.id);
  }
}

export interface ApplicationWorkqueueState {
  tab?: WorkQueueTab;
  search?: ApplicationSearchQuery;
  sort?: Sort;
  pageRequest?: PageRequest;
  page?: Page<Application>;
  selectedItems?: Array<number>;
  allSelected?: boolean;
  loading?: boolean;
}
