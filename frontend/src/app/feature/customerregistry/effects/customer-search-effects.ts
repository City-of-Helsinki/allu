import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {Observable, of} from 'rxjs';
import {Action} from '@ngrx/store';
import {catchError, concatMap, map, switchMap} from 'rxjs/operators';
import {CustomerService} from '@service/customer/customer.service';
import {
  CustomerSearchActionType,
  FindById,
  FindByIdSuccess,
  Search,
  SearchByType,
  SearchFailed,
  SearchSuccess
} from '../actions/customer-search-actions';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';

@Injectable()
export class CustomerSearchEffects {
  constructor(private actions: Actions,
              private customerService: CustomerService) {}

  
  customerSearch: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Search>(CustomerSearchActionType.Search),
    switchMap(action =>
      this.customerService.pagedSearch(action.payload.query, action.payload.sort, action.payload.pageRequest).pipe(
        map(customers => new SearchSuccess(action.targetType, customers)),
        catchError(error => of(new SearchFailed(action.targetType, error)))
      )
    )
  ));

  
  searchCustomerByType: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<SearchByType>(CustomerSearchActionType.SearchByType),
    concatMap(action => this.searchByType(action))
  ));

  
  findById: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<FindById>(CustomerSearchActionType.FindById),
    switchMap(action => this.customerService.findCustomerById(action.payload).pipe(
      map(customer => new FindByIdSuccess(action.targetType, customer)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

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
