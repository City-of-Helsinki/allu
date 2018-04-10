import {Injectable} from '@angular/core';
import {StoredFilterService} from './stored-filter.service';
import {StoredFilter} from '../../model/user/stored-filter';
import {StoredFilterType} from '../../model/user/stored-filter-type';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {NotificationService} from '../notification/notification.service';
import {Observable} from 'rxjs/Observable';
import {HttpResponse} from '../../util/http-response';
import {ArrayUtil} from '../../util/array-util';

export interface StoredFilterTypeState {
  current: number;
}

export interface StoredFilterState {
  MAP: StoredFilterTypeState;
  WORKQUEUE: StoredFilterTypeState;
  SUPERVISION_WORKQUEUE: StoredFilterTypeState;
  available: StoredFilter[];
}

export const initialState: StoredFilterState = {
  MAP: { current: undefined },
  WORKQUEUE: { current: undefined },
  SUPERVISION_WORKQUEUE: { current: undefined },
  available: []
};

@Injectable()
export class StoredFilterStore {

  private store = new BehaviorSubject<StoredFilterState>(initialState);

  constructor(private storedFilterService: StoredFilterService) {
    this.loadAvailable()
      .map(filters => this.createState(filters))
      .subscribe(state => this.store.next(state));
  }

  get changes(): Observable<StoredFilterState> {
    return this.store.asObservable().distinctUntilChanged();
  }

  get snapshot(): StoredFilterState {
    return this.store.getValue();
  }

  getCurrent(type: StoredFilterType): Observable<StoredFilter> {
    return this.changes
      .map(state => state[StoredFilterType[type]])
      .map(state => state.current)
      .switchMap(current => this.byId(current))
      .distinctUntilChanged();
  }

  getCurrentFilter(type: StoredFilterType): Observable<any> {
    return this.getCurrent(type)
      .filter(current => !!current)
      .map(storedFilter => storedFilter.filter);
  }

  getDefault(type: StoredFilterType): Observable<StoredFilter> {
    return this.getAvailable(type)
      .map(available => ArrayUtil.first(available, f => f.defaultFilter))
      .distinctUntilChanged();
  }

  getAvailable(type: StoredFilterType): Observable<StoredFilter[]> {
    return this.store.map(state => state.available)
      .map(available => available.filter(f => f.type === type))
      .distinctUntilChanged();
  }

  currentChange(filter: StoredFilter): void {
    const next = {...this.snapshot};
    next[filter.typeName].current = filter.id;
    this.store.next(next);
  }

  save(filter: StoredFilter): Observable<StoredFilter> {
    return this.storedFilterService.save(filter)
      .do(saved => this.loadAndSetCurrent(saved));
  }

  remove(id: number): Observable<HttpResponse> {
    return this.storedFilterService.remove(id)
      .do(() => this.loadAndClearCurrent(id));
  }

  private byId(id: number): Observable<StoredFilter> {
    return this.changes
      .map(state => state.available)
      .map(available => available.find(filter => filter.id === id));
  }

  private createState(filters: StoredFilter[]): StoredFilterState {
    const state = {...initialState};
    filters.forEach(filter => {
      if (filter.defaultFilter) {
        state[filter.typeName].current = filter.id;
      }

      state.available.push(filter);
    });
    return state;
  }

  private loadAndSetCurrent(filter: StoredFilter): void {
    const state = {...this.snapshot};
    this.loadAvailable().subscribe(available => {
      state[filter.typeName].current = filter.id;
      state.available = available;
      this.store.next(state);
    });
  }

  private loadAndClearCurrent(id: number): void {
    const state = {...this.snapshot};
    const removed = state.available.find(filter => filter.id === id);
    const typeState = state[removed.typeName];

    if (typeState.current === removed.id) {
      typeState.current = undefined;
    }

    this.loadAvailable().subscribe(available => {
      state.available = available;
      state[removed.typeName] = typeState;
      this.store.next(state);
    });
  }

  private loadAvailable(): Observable<StoredFilter[]> {
    return this.storedFilterService.findForCurrentUser()
      .catch(err => NotificationService.errorCatch(err, []));
  }
}
