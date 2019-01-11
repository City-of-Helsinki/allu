import {Injectable} from '@angular/core';
import {StoredFilterService} from './stored-filter.service';
import {StoredFilter} from '../../model/user/stored-filter';
import {StoredFilterType} from '../../model/user/stored-filter-type';
import {BehaviorSubject, Observable} from 'rxjs';
import {NotificationService} from '../../feature/notification/notification.service';
import {ArrayUtil} from '../../util/array-util';
import {catchError, distinctUntilChanged, filter, map, switchMap, tap} from 'rxjs/internal/operators';
import {SelectLayers} from '@feature/map/actions/map-layer-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Store} from '@ngrx/store';
import * as fromMap from '@feature/map/reducers';

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

  private state$ = new BehaviorSubject<StoredFilterState>(initialState);

  constructor(private storedFilterService: StoredFilterService,
              private notification: NotificationService,
              private store: Store<fromMap.State>) {
    this.loadAvailable().pipe(
      map(filters => this.createState(filters))
    ).subscribe(state => this.state$.next(state));
  }

  get changes(): Observable<StoredFilterState> {
    return this.state$.asObservable().pipe(distinctUntilChanged());
  }

  get snapshot(): StoredFilterState {
    return this.state$.getValue();
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
    return this.state$.pipe(
      map(state => state.available),
      map(available => available.filter(f => f.type === type)),
      distinctUntilChanged()
    );
  }

  currentChange(storedFilter: StoredFilter): void {
    const next = {...this.snapshot};
    next[storedFilter.typeName].current = storedFilter.id;
    this.state$.next(next);
  }

  currentMapFilterChange(storedFilter: StoredFilter): void {
    this.currentChange(storedFilter);
    this.store.dispatch(new SelectLayers(ActionTargetType.Home, storedFilter.filter.layers));
  }

  resetCurrent(type: StoredFilterType): void {
    const next = {...this.snapshot};
    next[StoredFilterType[type]].current = undefined;
    this.state$.next(next);
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
    const state = this.initDefaultFilter(initialState, filters);
    state.available = filters;
    return state;
  }

  private loadAndSetCurrent(storedFilter: StoredFilter): void {
    const state = {...this.snapshot};
    this.loadAvailable().subscribe(available => {
      state[storedFilter.typeName].current = storedFilter.id;
      state.available = available;
      this.state$.next(state);
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
      this.state$.next(state);
    });
  }

  private loadAvailable(): Observable<StoredFilter[]> {
    return this.storedFilterService.findForCurrentUser().pipe(
      catchError(err => this.notification.errorCatch(err, []))
    );
  }

  private initDefaultFilter(baseState: StoredFilterState, filters: StoredFilter[] = []): StoredFilterState {
    const state = {...baseState};
    filters
      .filter(f => f.defaultFilter)
      .forEach(defaultFilter => {
        state[defaultFilter.typeName].current = defaultFilter.id;

        if (defaultFilter.type === StoredFilterType.MAP) {
          this.store.dispatch(new SelectLayers(ActionTargetType.Home, defaultFilter.filter.layers));
        }
      });
    return state;

  }
}
