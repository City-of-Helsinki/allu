import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {SupervisionWorkItemStore} from '../supervision-work-item-store';
import {MatCheckboxChange, MatPaginator, MatSort} from '@angular/material';
import {ActivatedRoute, Router} from '@angular/router';
import {Subject} from 'rxjs/Subject';
import {SupervisionWorkItemDatasource} from './supervision-work-item-datasource';
import {SupervisionWorkItem} from '../../../model/application/supervision/supervision-work-item';
import {WorkQueueTab} from '../../workqueue/workqueue-tab';
import {Sort} from '../../../model/common/sort';
import {StoredFilterType} from '../../../model/user/stored-filter-type';
import {StoredFilterStore} from '../../../service/stored-filter/stored-filter-store';

@Component({
  selector: 'supervision-workqueue-content',
  templateUrl: './workqueue-content.component.html',
  styleUrls: ['./workqueue-content.component.scss']
})
export class WorkQueueContentComponent implements OnInit, OnDestroy {
  displayedColumns = [
    'selected', 'owner.realName', 'type', 'application.applicationId', 'streetAddress',
    'plannedFinishingTime', 'application.status', 'project.name', 'creator.realName'
  ];
  dataSource: SupervisionWorkItemDatasource;
  allSelected = false;
  length = 0;
  pageIndex = 0;
  loading = false;

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  private selectedItems: Array<number> = [];
  private destroy = new Subject<boolean>();

  constructor(private store: SupervisionWorkItemStore,
              private router: Router,
              private route: ActivatedRoute,
              private storedFilterStore: StoredFilterStore) {
  }

  ngOnInit(): void {
    this.sort.sort(Sort.toMatSortable(this.store.snapshot.sort));

    this.dataSource = new SupervisionWorkItemDatasource(this.store, this.paginator, this.sort);

    this.dataSource.page
      .takeUntil(this.destroy)
      .subscribe(page => {
        this.length = page.totalElements;
        this.pageIndex = page.pageNumber;
      });

    this.route.data
      .map(data => data.tab)
      .takeUntil(this.destroy)
      .subscribe((tab: string) => this.store.tabChange(WorkQueueTab[tab]));

    this.store.changes.map(state => state.selectedItems)
      .distinctUntilChanged()
      .takeUntil(this.destroy)
      .subscribe(selected => this.selectedItems = selected);

    this.store.changes.map(state => state.allSelected)
      .distinctUntilChanged()
      .takeUntil(this.destroy)
      .subscribe(allSelected => this.allSelected = allSelected);

    this.store.changes.map(state => state.loading)
      .distinctUntilChanged()
      .takeUntil(this.destroy)
      .subscribe(loading => this.loading = loading);

    this.storedFilterStore.getCurrentFilter(StoredFilterType.SUPERVISION_WORKQUEUE)
      .takeUntil(this.destroy)
      .map(filter => Sort.toMatSortable(filter.sort))
      .subscribe(sort => this.sort.sort(sort));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  selected(id: number): boolean {
    return this.selectedItems.indexOf(id) >= 0;
  }

  checkAll(change: MatCheckboxChange): void {
    this.store.toggleAll(change.checked);
  }

  checkSingle(change: MatCheckboxChange, taskId: number) {
    this.store.toggleSingle(taskId, change.checked);
  }

  trackById(index: number, item: SupervisionWorkItem) {
    return item.id;
  }
}
