import {Injectable} from '@angular/core';
import {StoredFilterService} from './stored-filter.service';
import {StoredFilter} from '../../model/user/stored-filter';
import {StoredFilterType} from '../../model/user/stored-filter-type';
import {BehaviorSubject, Observable} from 'rxjs';
import {NotificationService} from '../notification/notification.service';
import {ArrayUtil} from '../../util/array-util';
import {catchError, distinctUntilChanged, filter, map, switchMap, tap} from 'rxjs/internal/operators';

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

  constructor(private storedFilterService: StoredFilterService, private notification: NotificationService) {
    this.loadAvailable().pipe(
      map(filters => this.createState(filters))
    ).subscribe(state => this.store.next(state));
  }

  get changes(): Observable<StoredFilterState> {
    return this.store.asObservable().pipe(distinctUntilChanged());
  }

  get snapshot(): StoredFilterState {
    return this.store.getValue();
  }

  getCurrent(type: StoredFilterType): Observable<StoredFilter> {
    return this.changes.pipe(
      map(state => state[StoredFilterType[type]]),
      map(state => state.current),
      switchMap(current => this.byId(current)),
      distinctUntilChanged()
    );
  }

  getCurrentFilter(type: StoredFilterType): Observable<any> {
    return this.getCurrent(type).pipe(
      filter(current => !!current),
      map(storedFilter => storedFilter.filter)
    );
  }

  getDefault(type: StoredFilterType): Observable<StoredFilter> {
    return this.getAvailable(type).pipe(
      map(available => ArrayUtil.first(available, f => f.defaultFilter)),
      distinctUntilChanged()
    );
  }

  getAvailable(type: StoredFilterType): Observable<StoredFilter[]> {
    return this.store.pipe(
      map(state => state.available),
      map(available => available.filter(f => f.type === type)),
      distinctUntilChanged()
    );
  }

  currentChange(storedFilter: StoredFilter): void {
    const next = {...this.snapshot};
    next[storedFilter.typeName].current = storedFilter.id;
    this.store.next(next);
  }

  resetCurrent(type: StoredFilterType): void {
    const next = {...this.snapshot};
    next[StoredFilterType[type]].current = undefined;
    this.store.next(next);
  }

  save(storedFilter: StoredFilter): Observable<StoredFilter> {
    return this.storedFilterService.save(storedFilter).pipe(
      tap(saved => this.loadAndSetCurrent(saved))
    );
  }

  remove(id: number): Observable<{}> {
    return this.storedFilterService.remove(id).pipe(
      tap(() => this.loadAndClearCurrent(id))
    );
  }

  private byId(id: number): Observable<StoredFilter> {
    return this.changes.pipe(
      map(state => state.available),
      map(available => available.find(storedFilter => storedFilter.id === id))
    );
  }

  private createState(filters: StoredFilter[]): StoredFilterState {
    const state = {...initialState};
    filters.forEach(storedFilter => {
      if (storedFilter.defaultFilter) {
        state[storedFilter.typeName].current = storedFilter.id;
      }

      state.available.push(storedFilter);
    });
    return state;
  }

  private loadAndSetCurrent(storedFilter: StoredFilter): void {
    const state = {...this.snapshot};
    this.loadAvailable().subscribe(available => {
      state[storedFilter.typeName].current = storedFilter.id;
      state.available = available;
      this.store.next(state);
    });
  }

  private loadAndClearCurrent(id: number): void {
    const state = {...this.snapshot};
    const removed = state.available.find(storedFilter => storedFilter.id === id);
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
    return this.storedFilterService.findForCurrentUser().pipe(
      catchError(err => this.notification.errorCatch(err, []))
    );
  }
}
