import {Component, OnDestroy, OnInit} from '@angular/core';
import '../../../rxjs-extensions.ts';
import {SupervisionWorkItemStore} from '../supervision-work-item-store';
import {Observable} from 'rxjs/Observable';
import {SupervisionWorkItem} from '../../../model/application/supervision/supervision-work-item';
import {MatCheckboxChange} from '@angular/material';
import {Subscription} from 'rxjs/Subscription';
import {Page} from '../../../model/common/page';
import {Sort} from '../../../model/common/sort';

@Component({
  selector: 'supervision-workqueue-content',
  template: require('./workqueue-content.component.html'),
  styles: [require('./workqueue-content.component.scss')]
})
export class WorkQueueContentComponent implements OnInit, OnDestroy {

  workItems: Observable<Array<SupervisionWorkItem>>;
  page: Observable<Page<SupervisionWorkItem>>;
  allSelected: boolean = false;
  sort = new Sort();

  private selectedItems: Array<number> = [];
  private selectedItemsSubscription: Subscription;
  private allSelectedSubscription: Subscription;
  private sortSubscription: Subscription;

  constructor(private store: SupervisionWorkItemStore) {}

  ngOnInit(): void {
    this.page = this.store.changes.map(state => state.page).distinctUntilChanged();
    this.workItems = this.page.map(p => p.content);

    this.selectedItemsSubscription = this.store.changes.map(state => state.selectedItems)
      .distinctUntilChanged()
      .subscribe(selected => this.selectedItems = selected);

    this.allSelectedSubscription = this.store.changes.map(state => state.allSelected)
      .distinctUntilChanged()
      .subscribe(allSelected => this.allSelected = allSelected);

    this.sortSubscription = this.store.changes.map(state => state.sort)
      .distinctUntilChanged()
      .subscribe(sort => this.sort = sort);
  }

  ngOnDestroy(): void {
    this.selectedItemsSubscription.unsubscribe();
    this.allSelectedSubscription.unsubscribe();
    this.sortSubscription.unsubscribe();
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
}
