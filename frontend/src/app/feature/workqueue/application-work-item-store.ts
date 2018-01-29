import {Injectable} from '@angular/core';
import {WorkQueueTab} from '../workqueue/workqueue-tab';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';
import {HttpResponse} from '../../util/http-response';
import {ArrayUtil} from '../../util/array-util';
import {Page} from '../../model/common/page';
import {Sort} from '../../model/common/sort';
import {PageRequest} from '../../model/common/page-request';
import {ApplicationSearchQuery} from '../../model/search/ApplicationSearchQuery';
import {Application} from '../../model/application/application';
import '../../rxjs-extensions';
import {ApplicationService} from '../../service/application/application.service';

const initialState: ApplicationWorkqueueState = {
  tab: WorkQueueTab.OWN,
  search: new ApplicationSearchQuery(),
  sort: new Sort(),
  pageRequest: new PageRequest(),
  page: new Page<Application>(),
  selectedItems: [],
  allSelected: false
};

@Injectable()
export class ApplicationWorkItemStore {
  private store = new BehaviorSubject<ApplicationWorkqueueState>(initialState);

  constructor(private service: ApplicationService) {
    Observable.merge(
      this.changes.map(state => state.search).distinctUntilChanged(),
      this.changes.map(state => state.sort).distinctUntilChanged(),
      this.changes.map(state => state.pageRequest).distinctUntilChanged()
    ).subscribe(() => this.refresh());
  }

  get changes(): Observable<ApplicationWorkqueueState> {
    return this.store.asObservable().distinctUntilChanged();
  }

  public tabChange(tab: WorkQueueTab) {
    this.store.next({ ...this.store.getValue(), tab });
  }

  public searchChange(search: ApplicationSearchQuery) {
    this.store.next({ ...this.store.getValue(), search });
  }

  public sortChange(sort: Sort) {
    this.store.next({ ...this.store.getValue(), sort });
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

  public changeOwnerForSelected(ownerId: number): Observable<HttpResponse> {
    const selected = this.store.getValue().selectedItems;
    return this.service.changeOwner(ownerId, selected)
      .do(() => this.refresh());
  }

  public removeOwnerFromSelected(): Observable<HttpResponse> {
    const selected = this.store.getValue().selectedItems;
    return this.service.removeOwner(selected)
      .do(() => this.refresh());
  }

  private selectedItems(selected: Array<number>) {
    const itemIds = this.itemIds(this.store.getValue().page);
    this.store.next({
      ...this.store.getValue(),
      selectedItems: selected,
      allSelected: this.allSelected(itemIds, selected)
    });
  }

  private refresh(): void {
    this.selectedItems([]);
    this.pagedSearch().subscribe(page => this.pageChange(page));
  }

  private pagedSearch(): Observable<Page<Application>> {
    const state = this.store.getValue();
    if (state.tab === WorkQueueTab.COMMON) {
      return this.service.pagedSearchSharedByGroup(state.search, state.sort, state.pageRequest);
    } else {
      return this.service.pagedSearch(state.search, state.sort, state.pageRequest);
    }
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
}
