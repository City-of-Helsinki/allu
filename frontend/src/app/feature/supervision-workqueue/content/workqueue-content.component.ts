import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatLegacyPaginator as MatPaginator} from '@angular/material/legacy-paginator';
import {MatSort} from '@angular/material/sort';
import {ActivatedRoute} from '@angular/router';
import {Observable, Subject} from 'rxjs';
import {SupervisionWorkItemDatasource} from './supervision-work-item-datasource';
import {SupervisionWorkItem} from '@model/application/supervision/supervision-work-item';
import {WorkQueueTab} from '@feature/workqueue/workqueue-tab';
import {Sort} from '@model/common/sort';
import {StoredFilterType} from '@model/user/stored-filter-type';
import {StoredFilterStore} from '@service/stored-filter/stored-filter-store';
import {map, takeUntil} from 'rxjs/internal/operators';
import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromSupervisionWorkQueue from '@feature/supervision-workqueue/reducers';
import {ResetToFirstPage, ToggleSelect, ToggleSelectAll} from '@feature/application/supervision/actions/supervision-task-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {SetTab} from '@feature/workqueue/actions/workqueue-actions';

@Component({
  selector: 'supervision-workqueue-content',
  templateUrl: './workqueue-content.component.html',
  styleUrls: ['./workqueue-content.component.scss']
})
export class WorkQueueContentComponent implements OnInit, OnDestroy {
  displayedColumns = [
    'selected', 'owner.realName', 'type', 'application.applicationId', 'address',
    'plannedFinishingTime', 'application.status', 'creator.realName'
  ];
  dataSource: SupervisionWorkItemDatasource;
  allSelected$: Observable<boolean>;
  someSelected$: Observable<boolean>;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  private destroy = new Subject<boolean>();

  constructor(private store: Store<fromRoot.State>,
              private route: ActivatedRoute,
              private storedFilterStore: StoredFilterStore) {
  }

  ngOnInit(): void {
    this.dataSource = new SupervisionWorkItemDatasource(this.store, this.paginator, this.sort);

    this.route.data.pipe(
      map(data => data.tab),
      takeUntil(this.destroy)
    ).subscribe((tab: WorkQueueTab) => {
      this.store.dispatch(new SetTab(ActionTargetType.SupervisionTaskWorkQueue, tab));
      this.store.dispatch(new ResetToFirstPage(ActionTargetType.SupervisionTaskWorkQueue));
    });

    this.allSelected$ = this.store.pipe(select(fromSupervisionWorkQueue.getAllSelected));
    this.someSelected$ = this.store.pipe(select(fromSupervisionWorkQueue.getSomeSelected));

    this.storedFilterStore.getCurrentFilter(StoredFilterType.SUPERVISION_WORKQUEUE).pipe(
      takeUntil(this.destroy),
      map(filter => Sort.toMatSortable(filter.sort))
    ).subscribe(sort => this.sort.sort(sort));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  selected(id: number): Observable<boolean> {
    return this.store.pipe(
      select(fromSupervisionWorkQueue.getSelected),
      map(selected => selected.indexOf(id) >= 0)
    );
  }

  checkAll(): void {
    this.store.dispatch(new ToggleSelectAll(ActionTargetType.SupervisionTaskWorkQueue));
  }

  checkSingle(taskId: number) {
    this.store.dispatch(new ToggleSelect(ActionTargetType.SupervisionTaskWorkQueue, taskId));
  }

  trackById(index: number, item: SupervisionWorkItem) {
    return item.id;
  }
}
