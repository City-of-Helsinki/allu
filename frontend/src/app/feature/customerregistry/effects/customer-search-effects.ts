import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Observable, of} from 'rxjs';
import {Action} from '@ngrx/store';
import {catchError, concatMap, map, switchMap} from 'rxjs/operators';
import {CustomerService} from '@service/customer/customer.service';
import {CustomerSearchActionType, Search, SearchByType, SearchFailed, SearchSuccess} from '../actions/customer-search-actions';

@Injectable()
export class CustomerSearchEffects {
  constructor(private actions: Actions,
              private customerService: CustomerService) {}

  @Effect()
  customerSearch: Observable<Action> = this.actions.pipe(
    ofType<Search>(CustomerSearchActionType.Search),
    switchMap(action =>
      this.customerService.pagedSearch(action.payload.query, action.payload.sort, action.payload.pageRequest).pipe(
        map(customers => new SearchSuccess(action.targetType, customers)),
        catchError(error => of(new SearchFailed(action.targetType, error)))
      )
    )
  );

  @Effect()
  searchCustomerByType: Observable<Action> = this.actions.pipe(
    ofType<SearchByType>(CustomerSearchActionType.SearchByType),
    concatMap(action => this.searchByType(action))
  );

  private searchByType(action: SearchByType): Observable<Action> {
    return this.customerService.pagedSearchByType(
      action.payload.type,
      action.payload.query,
      action.payload.sort,
      action.payload.pageRequest).pipe(
      map(customers => new SearchSuccess(action.targetType, customers)),
      catchError(error => of(new SearchFailed(action.targetType, error)))
    );
  }
}
