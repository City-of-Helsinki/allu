import {Component, OnDestroy, OnInit} from '@angular/core';
import '../../../rxjs-extensions.ts';
import {SupervisionWorkItemStore} from '../supervision-work-item-store';
import {Observable} from 'rxjs/Observable';
import {SupervisionWorkItem} from '../../../model/application/supervision/supervision-work-item';

@Component({
  selector: 'supervision-workqueue-content',
  template: require('./workqueue-content.component.html'),
  styles: [require('./workqueue-content.component.scss')]
})
export class WorkQueueContentComponent implements OnInit, OnDestroy {

  workItems: Observable<Array<SupervisionWorkItem>>;

  constructor(private store: SupervisionWorkItemStore) {}

  ngOnInit(): void {
    this.workItems = this.store.changes.map(state => state.items);
  }

  ngOnDestroy(): void {
  }
}
