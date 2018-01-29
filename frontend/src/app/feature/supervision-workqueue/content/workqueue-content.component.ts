import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {SupervisionWorkItemStore} from '../supervision-work-item-store';
import {MatCheckboxChange, MatPaginator, MatSort} from '@angular/material';
import {Subscription} from 'rxjs/Subscription';
import {Sort} from '../../../model/common/sort';
import {Router} from '@angular/router';
import {Subject} from 'rxjs/Subject';
import {SupervisionWorkItemDatasource} from './supervision-work-item-datasource';
import {Some} from '../../../util/option';
import {EventUtil} from '../../../../../test/util/event-util';
import {SupervisionWorkItem} from '../../../model/application/supervision/supervision-work-item';

@Component({
  selector: 'supervision-workqueue-content',
  templateUrl: './workqueue-content.component.html',
  styleUrls: ['./workqueue-content.component.scss']
})
export class WorkQueueContentComponent implements OnInit, OnDestroy {
  displayedColumns = [
    'selected', 'type', 'application.applicationId', 'streetAddress',
    'plannedFinishingTime', 'application.status', 'project.name', 'user.realName'
  ];
  dataSource: SupervisionWorkItemDatasource;
  allSelected = false;

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  private selectedItems: Array<number> = [];
  private destroy = new Subject<boolean>();

  constructor(private store: SupervisionWorkItemStore, private router: Router) {
  }

  ngOnInit(): void {
    this.dataSource = new SupervisionWorkItemDatasource(this.store, this.paginator, this.sort);

    this.store.changes.map(state => state.selectedItems)
      .distinctUntilChanged()
      .takeUntil(this.destroy)
      .subscribe(selected => this.selectedItems = selected);

    this.store.changes.map(state => state.allSelected)
      .distinctUntilChanged()
      .takeUntil(this.destroy)
      .subscribe(allSelected => this.allSelected = allSelected);
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

  sortBy(sort: Sort) {
    this.store.sortChange(sort);
  }

  toApplicationTaskView(applicationId: number, event: any): void {
    if (EventUtil.targetHasClass(event, 'checkbox')) {
      this.router.navigate(['applications', applicationId, 'summary', 'supervision']);
    }
  }

  trackById(index: number, item: SupervisionWorkItem) {
    return item.id;
  }
}
