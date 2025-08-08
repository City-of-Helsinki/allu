import {SupervisionWorkItem} from '@model/application/supervision/supervision-work-item';
import {combineLatest} from 'rxjs';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';
import {filter, takeUntil} from 'rxjs/internal/operators';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import * as fromSupervisionWorkQueue from '@feature/supervision-workqueue/reducers';
import {Search, SetPaging, SetSort} from '@feature/application/supervision/actions/supervision-task-search-actions';
import {StoreDatasource} from '@service/common/store-datasource';

export class SupervisionWorkItemDatasource extends StoreDatasource<SupervisionWorkItem> {
  constructor(store: Store<fromRoot.State>,
              paginator: MatPaginator,
              sort: MatSort) {
    super(store, paginator, sort);
  }

  protected initSelectors(): void {
    this.actionTargetType = ActionTargetType.SupervisionTaskWorkQueue;
  }

  protected initTargetType(): void {
    this.resultSelector = fromSupervisionWorkQueue.getMatching;
    this.sortSelector = fromSupervisionWorkQueue.getSort;
    this.pageRequestSelector = fromSupervisionWorkQueue.getPageRequest;
    this.searchingSelector = fromSupervisionWorkQueue.getSearching;
  }

  protected dispatchPageRequest(pageRequest: PageRequest): void {
    this.store.dispatch(new SetPaging(this.actionTargetType, pageRequest));
  }

  protected dispatchSort(sort: Sort): void {
    this.store.dispatch(new SetSort(this.actionTargetType, sort));
  }

  protected setupSearch(): void {
    combineLatest([
      this.store.pipe(select(fromSupervisionWorkQueue.getParameters), filter(search => !!search)),
      this.store.pipe(select(this.sortSelector)),
      this.store.pipe(select(this.pageRequestSelector))
    ]).pipe(
      takeUntil(this.destroy)
    ).subscribe(([query, sort, pageRequest]) =>
      this.store.dispatch(new Search(ActionTargetType.SupervisionTaskWorkQueue, query, sort, pageRequest)));
  }
}
