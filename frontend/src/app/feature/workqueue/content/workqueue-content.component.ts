import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {MatCheckboxChange, MatDialog, MatPaginator, MatSort} from '@angular/material';

import {Application} from '../../../model/application/application';
import {CommentsModalComponent} from '../../application/comment/comments-modal.component';
import {ApplicationStatus} from '../../../model/application/application-status';
import {Subject} from 'rxjs/Subject';
import {ApplicationWorkItemStore} from '../application-work-item-store';
import {EventUtil} from '../../../../../test/util/event-util';
import {ApplicationWorkItemDatasource, ApplicationWorkItemRow} from './application-work-item-datasource';
import {SupervisionWorkItem} from '../../../model/application/supervision/supervision-work-item';
import {Some} from '../../../util/option';
import {WorkQueueTab} from '../workqueue-tab';
import {CityDistrictService} from '../../../service/map/city-district.service';
import {MapStore} from '../../../service/map/map-store';
import {Page} from '../../../model/common/page';

@Component({
  selector: 'workqueue-content',
  templateUrl: './workqueue-content.component.html',
  styleUrls: ['./workqueue-content.component.scss']
})
export class WorkQueueContentComponent implements OnInit, OnDestroy {
  displayedColumns = [
    'selected', 'owner.userName', 'applicationId', 'type', 'status', 'project.name',
    'customers.applicant.customer.name', 'locations.streetAddress', 'locations.cityDistrictId',
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

  constructor(private router: Router,
              private route: ActivatedRoute,
              private mapStore: MapStore,
              private cityDistrictService: CityDistrictService,
              private dialog: MatDialog,
              private store: ApplicationWorkItemStore) {
  }

  ngOnInit(): void {
    this.dataSource = new ApplicationWorkItemDatasource(this.store, this.paginator, this.sort);

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

     this.store.changes.map(state => state.search)
       .distinctUntilChanged()
       .takeUntil(this.destroy)
       .subscribe(query => this.selectedTags = query.tags);

    this.store.changes.map(state => state.loading)
      .distinctUntilChanged()
      .takeUntil(this.destroy)
      .subscribe(loading => this.loading = loading);
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

  toApplication(application: Application, event: any): void {
    if (EventUtil.targetHasClass(event, 'checkbox')) {
      this.router.navigate(this.getNavigation(application));
    }
  }

  showComments(applicationId: number): void {
    const dialogRef = this.dialog.open<CommentsModalComponent>(CommentsModalComponent, {
      disableClose: false, width: '800px'
    });
    dialogRef.componentInstance.applicationId = applicationId;
  }

  districtName(id: number): Observable<string> {
    return id !== undefined ? this.cityDistrictService.byId(id).map(d => d.name) : Observable.empty();
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

  private getNavigation(application: Application): Array<any> {
    if (ApplicationStatus[application.status] === ApplicationStatus.DECISIONMAKING) {
      return ['applications', application.id, 'decision'];
    } else {
      return ['applications', application.id, 'summary'];
    }
  }
}
