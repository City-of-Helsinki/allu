import {Component, OnDestroy, OnInit} from '@angular/core';
import '../../rxjs-extensions.ts';
import {WorkQueueTab} from '../workqueue/workqueue-tab';
import {SupervisionWorkItemStore} from './supervision-work-item-store';
import {MatTabChangeEvent} from '@angular/material';
import {SupervisionTaskService} from '../../service/supervision/supervision-task.service';

@Component({
  selector: 'supervision-workqueue',
  template: require('./workqueue.component.html'),
  styles: [
    require('./workqueue.component.scss')
  ]
})
export class WorkQueueComponent implements OnInit, OnDestroy {
  tabs = [WorkQueueTab[WorkQueueTab.OWN], WorkQueueTab[WorkQueueTab.COMMON]];

  constructor(
    private store: SupervisionWorkItemStore,
    private taskService: SupervisionTaskService) {
  }

  ngOnInit() {
    this.store.changes.map(state => state.search)
      .distinctUntilChanged()
      .switchMap(search => this.taskService.search(search))
      .subscribe(items => this.store.update({items: items}));
  }

  ngOnDestroy() {
  }

  tabSelected(event: MatTabChangeEvent) {
    this.store.update({tab: WorkQueueTab[this.tabs[event.index]]});
  }
}
