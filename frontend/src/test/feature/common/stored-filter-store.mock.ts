import {initialState, StoredFilterState} from '../../../app/service/stored-filter/stored-filter-store';
import {StoredFilter} from '../../../app/model/user/stored-filter';
import {StoredFilterType} from '../../../app/model/user/stored-filter-type';
import {BehaviorSubject, EMPTY, Observable, of} from 'rxjs/index';

export class StoredFilterStoreMock {
  public store = new BehaviorSubject<StoredFilterState>(initialState);

  get changes(): Observable<StoredFilterState> {
    return this.store.asObservable();
  }

  get snapshot(): StoredFilterState {
    return this.store.getValue();
  }

  getCurrent(type: StoredFilterType): Observable<StoredFilter> {
    return of(new StoredFilter());
  }

  getCurrentFilter(type: StoredFilterType): Observable<any> {
    return EMPTY;
  }

  getDefault(type: StoredFilterType): Observable<StoredFilter> {
    return of(new StoredFilter());
  }

  getAvailable(type: StoredFilterType): Observable<StoredFilter[]> {
    return of([]);
  }
}
