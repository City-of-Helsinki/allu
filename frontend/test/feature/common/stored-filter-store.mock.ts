import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {initialState, StoredFilterState} from '../../../src/app/service/stored-filter/stored-filter-store';
import {StoredFilter} from '../../../src/app/model/user/stored-filter';
import {Observable} from 'rxjs/Observable';
import {StoredFilterType} from '../../../src/app/model/user/stored-filter-type';

export class StoredFilterStoreMock {
  public store = new BehaviorSubject<StoredFilterState>(initialState);

  get changes(): Observable<StoredFilterState> {
    return this.store.asObservable();
  }

  get snapshot(): StoredFilterState {
    return this.store.getValue();
  }

  getCurrent(type: StoredFilterType): Observable<StoredFilter> {
    return Observable.of(new StoredFilter());
  }

  getCurrentFilter(type: StoredFilterType): Observable<any> {
    return Observable.empty();
  }

  getDefault(type: StoredFilterType): Observable<StoredFilter> {
    return Observable.of(new StoredFilter());
  }

  getAvailable(type: StoredFilterType): Observable<StoredFilter[]> {
    return Observable.of([]);
  }
}
