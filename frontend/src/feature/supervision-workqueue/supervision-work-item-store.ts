import {Injectable} from '@angular/core';
import {WorkQueueTab} from '../workqueue/workqueue-tab';
import {SupervisionTaskSearchCriteria} from '../../model/application/supervision/supervision-task-search-criteria';
import {SupervisionWorkItem} from '../../model/application/supervision/supervision-work-item';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';
import {SupervisionTaskService} from '../../service/supervision/supervision-task.service';
import {HttpResponse} from '../../util/http-response';
import {ArrayUtil} from '../../util/array-util';

const initialState: SupervisionWorkqueueState = {
  tab: WorkQueueTab.OWN,
  search: new SupervisionTaskSearchCriteria(),
  items: [],
  selectedItems: [],
  allSelected: false
};

@Injectable()
export class SupervisionWorkItemStore {
  private store = new BehaviorSubject<SupervisionWorkqueueState>(initialState);

  constructor(private service: SupervisionTaskService) {
    this.changes.map(state => state.search)
      .distinctUntilChanged()
      .subscribe(search => this.refresh());
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

  public itemsChange(items: Array<SupervisionWorkItem>) {
    const selected = this.store.getValue().selectedItems;
    const itemIds = items.map(item => item.id);
    this.update({
      items: items,
      allSelected: this.allSelected(itemIds, selected)
    });
  }

  public toggleAll(checked: boolean) {
    if (checked) {
      const items = this.store.getValue().items.map(item => item.id);
      this.selectedItems(items);
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
    const itemIds = this.store.getValue().items.map(item => item.id);
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
    const search = this.store.getValue().search;
    this.selectedItems([]);
    this.service.search(search).subscribe(items => this.itemsChange(items));
  }

  private allSelected(items: Array<number>, selected: Array<number>): boolean {
    const hasItems = items.length > 0;
    return hasItems && ArrayUtil.containSame(items, selected);
  }
}

export interface SupervisionWorkqueueState {
  tab?: WorkQueueTab;
  search?: SupervisionTaskSearchCriteria;
  items?: Array<SupervisionWorkItem>;
  selectedItems?: Array<number>;
  allSelected?: boolean;
}
