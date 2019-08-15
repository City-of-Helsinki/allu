import {SupervisionWorkItem} from '@model/application/supervision/supervision-work-item';
import {combineLatest, Observable} from 'rxjs';
import {MatPaginator, MatSort} from '@angular/material';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';
import {filter, map, takeUntil} from 'rxjs/internal/operators';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import * as fromSupervisionWorkQueue from '@feature/supervision-workqueue/reducers';
import * as fromAuth from '@feature/auth/reducers';
import {Search, SetPaging, SetSort} from '@feature/application/supervision/actions/supervision-task-search-actions';
import {StoreDatasource} from '@service/common/store-datasource';
import {SupervisionTaskSearchCriteria} from '@model/application/supervision/supervision-task-search-criteria';
import {ObjectUtil} from '@util/object.util';
import {WorkQueueTab} from '@feature/workqueue/workqueue-tab';

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
    combineLatest(
      this.searchParameters,
      this.store.pipe(select(this.sortSelector)),
      this.store.pipe(select(this.pageRequestSelector))
    ).pipe(
      takeUntil(this.destroy)
    ).subscribe(([query, sort, pageRequest]) =>
      this.store.dispatch(new Search(ActionTargetType.SupervisionTaskWorkQueue, query, sort, pageRequest)));
  }

  private get searchParameters(): Observable<SupervisionTaskSearchCriteria> {
    return combineLatest(
      this.store.pipe(select(fromSupervisionWorkQueue.getParameters), filter(search => !!search)),
      this.store.pipe(select(fromSupervisionWorkQueue.getTab)),
      this.store.pipe(select(fromAuth.getUser), filter(user => !!user))
    ).pipe(
      map(([query, tab, user]) => {
        const queryCopy = ObjectUtil.clone(query);
        queryCopy.owners = WorkQueueTab.OWN === tab ? [user.id] : query.owners;
        return queryCopy;
      })
    );
  }
}
