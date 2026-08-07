import {initialState, StoredFilterState} from '../../../app/service/stored-filter/stored-filter-store';
import {StoredFilter} from '../../../app/model/user/stored-filter';
import {StoredFilterType} from '../../../app/model/user/stored-filter-type';
import {BehaviorSubject, EMPTY, Observable, of} from 'rxjs';

export class StoredFilterStoreMock {
  public store = new BehaviorSubject<StoredFilterState>(initialState);

  get changes(): Observable<StoredFilterState> {
    return this.store.asObservable();
  }

  get snapshot(): StoredFilterState {
    return this.store.getValue();
  }

  getCurrent(_type: StoredFilterType): Observable<StoredFilter> {
    return of(new StoredFilter());
  }

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  getCurrentFilter(_type: StoredFilterType): Observable<any> {
    return EMPTY;
  }

  getDefault(_type: StoredFilterType): Observable<StoredFilter> {
    return of(new StoredFilter());
  }

  getAvailable(_type: StoredFilterType): Observable<StoredFilter[]> {
    return of([]);
  }
}
