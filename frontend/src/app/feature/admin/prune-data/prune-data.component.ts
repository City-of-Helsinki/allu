import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {ConfirmDialogComponent} from '../../common/confirm-dialog/confirm-dialog.component';

import * as fromRoot from '@feature/allu/reducers';
import {Store} from '@ngrx/store';
import {Subject} from 'rxjs';
import {pruneDataTabs} from './prune-data-tab';
import {ActivatedRoute, Router} from '@angular/router';
import {filter, first, map, take, takeUntil} from 'rxjs/operators';
import {MatSort, Sort} from '@angular/material/sort';
import * as PruneDataActions from './store/prune-data.actions';
import {
  selectAllSelected,
  selectCurrentTab,
  selectDeleteInProgress,
  selectDeleteModalVisibility,
  selectFilteredData,
  selectPageIndex,
  selectPageSize,
  selectSelectedIds,
  selectSomeSelected,
  selectTotalItems
} from './store/prune-data.selectors';
import {MatPaginator, PageEvent} from '@angular/material/paginator';


interface ColumnConfig {
  columns: string[];
  translations: { [key: string]: string };
}

@Component({
  selector: 'prune-data',
  templateUrl: './prune-data.component.html',
  styleUrls: ['./prune-data.component.scss']
})
export class PruneDataComponent implements OnInit, OnDestroy {
  title = 'Tiedon poisto';
  filteredDataSource$ = this.store.select(selectFilteredData);
  someSelected$ = this.store.select(selectSomeSelected);
  allSelected$ = this.store.select(selectAllSelected);
  selectedIds$ = this.store.select(selectSelectedIds);
  selectedTab$ = this.store.select(selectCurrentTab);
  deleteModalVisible = this.store.select(selectDeleteModalVisibility);
  deleteInProgress = this.store.select(selectDeleteInProgress);
  totalItems$ = this.store.select(selectTotalItems);
  pageIndex$ = this.store.select(selectPageIndex);
  pageSize$ = this.store.select(selectPageSize);
  tabs = pruneDataTabs;

  hasSelectedItems$ = this.selectedIds$.pipe(
    map(selectedIds => selectedIds.length > 0));

  userColumns: string[] = ['selected', 'name'];
  applicationColumns: string[] = ['selected', 'applicationId', 'startTime', 'endTime', 'changeTime', 'changeType'];


  displayedColumns: string[] = [];
  currentTab$ = this.route.params.pipe(
    map(params => {
      return params['tab'];
    })
  );

  private destroy$ = new Subject<void>();

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private store: Store<fromRoot.State>,
    private router: Router,
    private route: ActivatedRoute,
    private dialog: MatDialog,
  ) {
  }

  ngOnInit(): void {
    // Handle tab changes
    this.currentTab$
      .pipe(takeUntil(this.destroy$))
      .subscribe(tab => {
        this.store.dispatch(PruneDataActions.setCurrentTab({ tab }));
        if (tab === 'user_data') { this.displayedColumns = this.userColumns; }
        if (tab !== 'user_data') { this.displayedColumns = this.applicationColumns; }
        this.loadData(tab, 0, 10);
    });

    this.sort?.sortChange
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
      if (this.paginator) {
          this.paginator.pageIndex = 0;
          this.currentTab$.pipe(take(1)).subscribe(tab => {
          this.loadData(tab, 0, this.paginator.pageSize);
      });
     }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadData(tab: string, page: number, size: number, sortField?: string, sortDirection?: string): void {
    this.store.dispatch(PruneDataActions.fetchAllData({
      tab,
      page,
      size,
      sortField,
      sortDirection
      }));
  }


  trackById<T extends { id: number }>(index: number, item: T): number {
    return item.id;
  }

  checkAll(): void {
    this.currentTab$.pipe(take(1)).subscribe(tab => {
      this.store.dispatch(PruneDataActions.toggleSelectAll({ tab }));
    });
  }

  checkSingle(id: number): void {
    this.currentTab$.pipe(take(1)).subscribe(tab => {
      this.store.dispatch(PruneDataActions.toggleSelectItem({ id, tab }));
    });
  }

  isSelected(id: number): any {
    return this.store.select(selectSelectedIds).pipe(
      map(selectedIds => selectedIds.includes(id))
    );
  }

  onSort(sort: Sort): void {
    const { active, direction } = sort;

    if (!direction) {
      this.currentTab$.pipe(take(1)).subscribe(tab => {
        this.loadData(tab, 0, this.paginator.pageSize);
      });
      return;
    }

    this.currentTab$.pipe(take(1)).subscribe(tab => {
      this.loadData(tab, 0, this.paginator.pageSize, active, direction);
      });
    }

  compare(a: any, b: any, isAsc: boolean): number {
    return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
  }

  deleteSelected(): void {
    const data = {
      title: 'Vahvista hakemusten anonymisointi',
      description: 'valitut hakemukset anonymisoidaan',
      confirmText: 'Anonymisoi',
      cancelText: 'Peruuta'
    };
    this.dialog.open(ConfirmDialogComponent, {data})
      .afterClosed()
      .pipe(filter(result => result)) // Ignore no answers
      .subscribe(() => this.handleDelete());
  }

  handleDelete(): void {
    this.selectedIds$.pipe(first()).subscribe((ids) => {
      this.store.dispatch(PruneDataActions.deleteData({ids}));
    });
  }

  onPageChange(event: PageEvent): void {
    this.currentTab$.pipe(take(1)).subscribe(tab => {
    this.loadData(tab, event.pageIndex, event.pageSize);
    });
  }
}
