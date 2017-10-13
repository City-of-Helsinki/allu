import {Injectable} from '@angular/core';
import {WorkQueueTab} from '../workqueue/workqueue-tab';
import {SupervisionTaskSearchCriteria} from '../../model/application/supervision/supervision-task-search-criteria';
import {SupervisionWorkItem} from '../../model/application/supervision/supervision-work-item';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Observable} from 'rxjs/Observable';

const initialState: SupervisionWorkqueueState = {
  tab: WorkQueueTab.OWN,
  search: new SupervisionTaskSearchCriteria(),
  items: []
};

@Injectable()
export class SupervisionWorkItemStore {
  private store = new BehaviorSubject<SupervisionWorkqueueState>(initialState);

  constructor() {}

  get changes(): Observable<SupervisionWorkqueueState> {
    return this.store.asObservable().distinctUntilChanged();
  }

  public update(newState: SupervisionWorkqueueState) {
    const oldState = this.store.getValue();
    this.store.next({...oldState, ...newState});
  }
}

export interface SupervisionWorkqueueState {
  tab?: WorkQueueTab;
  search?: SupervisionTaskSearchCriteria;
  items?: Array<SupervisionWorkItem>;
}
