import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Observable, Subject} from 'rxjs';
import {MatCheckboxChange, MatDialog, MatPaginator, MatSort} from '@angular/material';

import {Application} from '../../../model/application/application';
import {CommentsModalComponent} from '../../comment/comments-modal.component';
import {ApplicationStatus} from '../../../model/application/application-status';
import {ApplicationWorkItemStore} from '../application-work-item-store';
import {ApplicationWorkItemDatasource, ApplicationWorkItemRow} from './application-work-item-datasource';
import {SupervisionWorkItem} from '../../../model/application/supervision/supervision-work-item';
import {Some} from '../../../util/option';
import {WorkQueueTab} from '../workqueue-tab';
import {Sort} from '../../../model/common/sort';
import {StoredFilterType} from '../../../model/user/stored-filter-type';
import {StoredFilterStore} from '../../../service/stored-filter/stored-filter-store';
import {NotificationService} from '../../notification/notification.service';
import * as fromRoot from '../../allu/reducers';
import {Store} from '@ngrx/store';
import {distinctUntilChanged, map, takeUntil} from 'rxjs/internal/operators';

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
  allSelected = false;
  selectedTags: Array<string> = [];
  hoveredRowIndex: number;
  length = 0;
  pageIndex = 0;
  loading = false;

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  private selectedItems: Array<number> = [];
  private destroy = new Subject<boolean>();

  constructor(private route: ActivatedRoute,
              private store: Store<fromRoot.State>,
              private dialog: MatDialog,
              private itemStore: ApplicationWorkItemStore,
              private storedFilterStore: StoredFilterStore,
              private notification: NotificationService) {
  }

  ngOnInit(): void {
    this.sort.sort(Sort.toMatSortable(this.itemStore.snapshot.sort));

    this.dataSource = new ApplicationWorkItemDatasource(this.itemStore, this.notification, this.paginator, this.sort);

    this.dataSource.page.pipe(takeUntil(this.destroy))
      .subscribe(page => {
        this.length = page.totalElements;
        this.pageIndex = page.pageNumber;
      });

    this.route.data.pipe(
      map(data => data.tab),
      takeUntil(this.destroy)
    ).subscribe((tab: string) => this.itemStore.tabChange(WorkQueueTab[tab]));

    this.itemStore.changes.pipe(
      map(state => state.selectedItems),
      distinctUntilChanged(),
      takeUntil(this.destroy)
    ).subscribe(selected => this.selectedItems = selected);

    this.itemStore.changes.pipe(
      map(state => state.allSelected),
      distinctUntilChanged(),
      takeUntil(this.destroy)
    ).subscribe(allSelected => this.allSelected = allSelected);

     this.itemStore.changes.pipe(
       map(state => state.search),
       distinctUntilChanged(),
       takeUntil(this.destroy)
     ).subscribe(query => this.selectedTags = query.tags);

    this.itemStore.changes.pipe(
      map(state => state.loading),
      distinctUntilChanged(),
      takeUntil(this.destroy)
    ).subscribe(loading => this.loading = loading);

    this.storedFilterStore.getCurrentFilter(StoredFilterType.WORKQUEUE).pipe(
      takeUntil(this.destroy),
      map(filter => Sort.toMatSortable(filter.sort))
    ).subscribe(sort => this.sort.sort(sort));
  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  selected(id: number): boolean {
    return this.selectedItems.indexOf(id) >= 0;
  }

  checkAll(change: MatCheckboxChange): void {
    this.itemStore.toggleAll(change.checked);
  }

  checkSingle(change: MatCheckboxChange, taskId: number) {
    this.itemStore.toggleSingle(taskId, change.checked);
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

  isTagRow(index: number, row: ApplicationWorkItemRow): boolean {
    return Array.isArray(row.content);
  }

  tagSelected(tagName: string): boolean {
    return Some(this.selectedTags).map(selected => selected.indexOf(tagName) >= 0).orElse(false);
  }

  onMouseEnter(index: number): void {
    this.hoveredRowIndex = index;
  }

  onMouseLeave(index: number): void {
    this.hoveredRowIndex = undefined;
  }

  highlight(index: number, row: ApplicationWorkItemRow) {
    const isHoveredRow = this.hoveredRowIndex === index;
    const isRelatedRow = this.hoveredRowIndex === row.relatedIndex;
    return this.hoveredRowIndex !== undefined && (isHoveredRow || isRelatedRow);
  }

  hasTagRow(index: number, row: ApplicationWorkItemRow) {
    return Some(row.relatedIndex).map(relatedIndex => relatedIndex > index).orElse(false);
  }
}
