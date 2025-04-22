import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Application } from '@app/model/application/application';
import {MatLegacyDialog as MatDialog} from '@angular/material/legacy-dialog';
import {ConfirmDialogComponent} from '../../common/confirm-dialog/confirm-dialog.component';

import * as fromRoot from '@feature/allu/reducers';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { pruneDataTabs } from './prune-data-tab';
import { Router, ActivatedRoute } from '@angular/router';
import { map, switchMap, takeUntil, take, filter, first } from 'rxjs/operators';
import { MatSort, Sort } from '@angular/material/sort';
import * as PruneDataActions from './store/prune-data.actions';
import { selectAllSelected, selectPruneData, selectSomeSelected, selectSelectedIds, selectCurrentTab, selectFilteredData, selectDeleteModalVisibility, selectDeleteInProgress } from './store/prune-data.selectors';
import {MatLegacyPaginator as MatPaginator, LegacyPageEvent as PageEvent} from '@angular/material/legacy-paginator';


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
  title = 'Tiedon poisto'
  filteredDataSource$ = this.store.select(selectFilteredData)
  someSelected$ = this.store.select(selectSomeSelected);
  allSelected$ = this.store.select(selectAllSelected);
  selectedIds$ = this.store.select(selectSelectedIds);
  selectedTab$ = this.store.select(selectCurrentTab);
  deleteModalVisible = this.store.select(selectDeleteModalVisibility);
  deleteInProgress = this.store.select(selectDeleteInProgress);
  tabs = pruneDataTabs;

  hasSelectedItems$ = this.selectedIds$.pipe(
    map(selectedIds => selectedIds.length > 0))

  displayedColumns: string[] = ['selected', 'applicationId', 'startTime', 'endTime', 'changeTime', 'changeType'];
  currentTab$ = this.route.params.pipe(
    map(params => {
      const tab = params['tab'];
      return tab;
    })
  );

  private destroy$ = new Subject<void>();

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  pageSize = 10;
  pageSizeOptions: number[] = [5, 10, 25, 100];
  pageIndex = 0;

  constructor(
    private store: Store<fromRoot.State>,
    private router: Router,
    private route: ActivatedRoute,
    private dialog: MatDialog,
  ) {
  }

  ngOnInit(): void {
    // Initial data load
    this.store.dispatch(PruneDataActions.fetchAllData({ tab: 'applications' }));

    // Handle tab changes
    this.currentTab$
    .pipe(takeUntil(this.destroy$))
    .subscribe(tab => {
      this.store.dispatch(PruneDataActions.setCurrentTab({ tab }));
    });

    this.filteredDataSource$
    .pipe(takeUntil(this.destroy$))
    .subscribe(data => {
      if (this.paginator) {
        const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
        const endIndex = startIndex + this.paginator.pageSize;
        this.store.dispatch(PruneDataActions.updatePagination({
          pageIndex: this.paginator.pageIndex,
          pageSize: this.paginator.pageSize,
          data: data.slice(startIndex, endIndex)
        }));
      }
    })
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  trackById(index: number, item: Application) {
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
      this.store.dispatch(PruneDataActions.tableSortReset());
      return;
    }

    this.filteredDataSource$
    .pipe(take(1))
    .subscribe((data) => {
      const sortedData = [...data].sort((a, b) => {
        const isAsc = direction === 'asc';
        return this.compare(a[active], b[active], isAsc);
      });
      
      this.store.dispatch(PruneDataActions.tableSortChange({data: sortedData}));
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
      this.store.dispatch(PruneDataActions.deleteData({ids}))
    });
  }

  onPageChange(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
  }

}