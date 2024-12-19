import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Application } from '@app/model/application/application';
import { TimeUtil } from '../../../util/time.util';

import * as fromRoot from '@feature/allu/reducers';
import { Store } from '@ngrx/store';
import { Observable, Subject } from 'rxjs';
import { pruneDataTabs } from './prune-data-tab';
import { Router, ActivatedRoute } from '@angular/router';
import { map, switchMap, takeUntil, take } from 'rxjs/operators';
import { MatSort } from '@angular/material/sort';
import * as PruneDataActions from './store/prune-data.actions';
import { selectAllSelected, selectPruneData, selectSomeSelected } from './store/prune-data.selectors';

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
  dataSource$ = this.store.select(selectPruneData);
  someSelected$ = this.store.select(selectSomeSelected);
  allSelected$ = this.store.select(selectAllSelected);
  tabs = pruneDataTabs;

  private columnConfigs: { [key: string]: ColumnConfig } = {
    'applications': {
      columns: ['applicationId', 'startTime', 'endTime', 'changeTime', 'changeType'],
      translations: {}
    },
    'area-rentals': {
      columns: ['applicationId', 'startTime', 'endTime', 'changeTime', 'changeType'],
      translations: {}
    },
    'cable-reports': {
      columns: ['applicationId', 'startTime', 'endTime', 'changeTime', 'changeType'],
      translations: {}
    },
    'excavation-announcements': {
      columns: ['applicationId', 'startTime', 'endTime', 'changeTime', 'changeType'],
      translations: {}
    },
    'short-term-rentals': {
      columns: ['applicationId', 'startTime', 'endTime', 'changeTime', 'changeType'],
      translations: {}
    },
    'placement-contracts': {
      columns: ['applicationId', 'startTime', 'endTime', 'changeTime', 'changeType'],
      translations: {}
    },
    'events': {
      columns: ['applicationId', 'startTime', 'endTime', 'changeTime', 'changeType'],
      translations: {}
    },
    'traffic-arrangements': {
      columns: ['applicationId', 'startTime', 'endTime', 'changeTime', 'changeType'],
      translations: {}
    },
    'user_data': {
      columns: ['userId', 'firstName', 'lastName', 'email', 'phoneNumber', 'modifiedAt'],
      translations: {}
    }
  };

  displayedColumns: string[] = this.columnConfigs['applications'].columns;

  currentTab$ = this.route.params.pipe(
    map(params => {
      const tab = params['tab'];
      this.displayedColumns = this.columnConfigs[tab]?.columns || this.columnConfigs['applications'].columns;
      return tab;
    })
  );

  private destroy$ = new Subject<void>();

  @ViewChild(MatSort) sort: MatSort;


  constructor(
    private store: Store<fromRoot.State>,
    private router: Router,
    private route: ActivatedRoute,
  ) {
    // Remove the existing subscription and use the currentTab$ observable
  }

  ngOnInit(): void {
    // Initial data load
    this.store.dispatch(PruneDataActions.fetchAllData({ tab: 'applications' }));

    // Handle tab changes
    this.currentTab$
    .pipe(takeUntil(this.destroy$))
    .subscribe(tab => {
      this.store.dispatch(PruneDataActions.setCurrentTab({ tab }));
      this.store.dispatch(PruneDataActions.fetchAllData({ tab }));
    });
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

  isSelected(id: number): boolean {
    return true;
    // return this.store.select(selectSelectedIds).pipe(
    //   map(selectedIds => selectedIds.includes(id))
    // );
  }

  deleteSelected(): void {
    // const selectedIds = ... // logic to get selected ids
    // this.store.dispatch(PruneDataActions.deleteData({ ids: selectedIds }));
  }

  addToBasket(): void {
    // this.store.pipe(
    //   select(fromApplication.getSelectedApplications),
    //   take(1)
    // ).subscribe(selected => {
    //   this.store.dispatch(new AddMultiple(selected));
    //   this.store.dispatch(new ClearSelected(ActionTargetType.Application));
    // });
  }

}