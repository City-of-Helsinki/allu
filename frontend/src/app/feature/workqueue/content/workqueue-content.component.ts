import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Observable, Subject} from 'rxjs';
import {MatDialog, MatPaginator, MatSort} from '@angular/material';
import {CommentsModalComponent} from '@feature/comment/comments-modal.component';
import {ApplicationWorkItemDatasource} from './application-work-item-datasource';
import {SupervisionWorkItem} from '@model/application/supervision/supervision-work-item';
import {WorkQueueTab} from '@feature/workqueue/workqueue-tab';
import {Sort} from '@model/common/sort';
import {StoredFilterType} from '@model/user/stored-filter-type';
import {StoredFilterStore} from '@service/stored-filter/stored-filter-store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromWorkQueue from '@feature/workqueue/reducers';
import {select, Store} from '@ngrx/store';
import {map, takeUntil} from 'rxjs/operators';
import {ResetToFirstPage, ToggleSelect, ToggleSelectAll} from '@feature/application/actions/application-search-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {SetTab} from '@feature/workqueue/actions/workqueue-actions';
import {ApplicationTagType} from '@model/application/tag/application-tag-type';

@Component({
  selector: 'workqueue-content',
  templateUrl: './workqueue-content.component.html',
  styleUrls: ['./workqueue-content.component.scss']
})
export class WorkQueueContentComponent implements OnInit, OnDestroy {
  displayedColumns = [
    'selected', 'owner.userName', 'applicationId', 'type', 'status', 'project.identifier',
    'customers.applicant.customer.name', 'locations.address', 'locations.cityDistrictId',
    'creationTime', 'startTime', 'comments'
  ];
  dataSource: ApplicationWorkItemDatasource;
  allSelected$: Observable<boolean>;
  someSelected$: Observable<boolean>;
  selectedTags$: Observable<ApplicationTagType[]>;
  hoveredRowIndex: number;
  length = 0;
  pageIndex = 0;
  loading = false;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  private destroy = new Subject<boolean>();

  constructor(private route: ActivatedRoute,
              private store: Store<fromRoot.State>,
              private dialog: MatDialog,
              private storedFilterStore: StoredFilterStore) {
  }

  ngOnInit(): void {
    this.dataSource = new ApplicationWorkItemDatasource(this.store, this.paginator, this.sort);

    this.route.data.pipe(
      map(data => data.tab),
      takeUntil(this.destroy)
    ).subscribe((tab: WorkQueueTab) => {
      this.store.dispatch(new SetTab(ActionTargetType.ApplicationWorkQueue, tab));
      this.store.dispatch(new ResetToFirstPage(ActionTargetType.ApplicationWorkQueue));
    });

    this.allSelected$ = this.store.pipe(select(fromWorkQueue.getAllApplicationsSelected));
    this.someSelected$ = this.store.pipe(select(fromWorkQueue.getSomeApplicationsSelected));
    this.selectedTags$ = this.store.pipe(
      select(fromWorkQueue.getApplicationSearchParameters),
      map(search => search.tags)
    );

    this.storedFilterStore.getCurrentFilter(StoredFilterType.WORKQUEUE).pipe(
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
      select(fromWorkQueue.getSelectedApplications),
      map(selected => selected.indexOf(id) >= 0)
    );
  }

  checkAll(): void {
    this.store.dispatch(new ToggleSelectAll(ActionTargetType.ApplicationWorkQueue));
  }

  checkSingle(id: number) {
    this.store.dispatch(new ToggleSelect(ActionTargetType.ApplicationWorkQueue, id));
  }

  showComments(applicationId: number): void {
    const dialogRef = this.dialog.open<CommentsModalComponent>(CommentsModalComponent, {
      disableClose: false, width: '800px'
    });
    dialogRef.componentInstance.applicationId = applicationId;
  }

  districtName(id: number): Observable<string> {
    return this.store.select(fromRoot.getCityDistrictName(id));
  }

  trackById(index: number, item: SupervisionWorkItem) {
    return item.id;
  }

  tagSelected(tag: ApplicationTagType): Observable<boolean> {
    return this.selectedTags$.pipe(
      map(selected => selected.indexOf(tag) >= 0)
    );
  }

  onMouseEnter(index: number): void {
    this.hoveredRowIndex = index;
  }

  onMouseLeave(): void {
    this.hoveredRowIndex = undefined;
  }

  highlight(index: number) {
    const isHoveredRow = this.hoveredRowIndex === index;
    return this.hoveredRowIndex !== undefined && isHoveredRow;
  }
}
