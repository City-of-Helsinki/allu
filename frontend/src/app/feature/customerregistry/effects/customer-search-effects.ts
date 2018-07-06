import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Observable, of} from 'rxjs';
import {Action} from '@ngrx/store';
import {catchError, map, switchMap} from 'rxjs/operators';
import {CustomerService} from '../../../service/customer/customer.service';
import {
  CustomerSearchActionType,
  Search,
  SearchFailed,
  SearchSuccess
} from '../actions/customer-search-actions';

@Injectable()
export class CustomerSearchEffects {
  constructor(private actions: Actions,
              private customerService: CustomerService) {}

  @Effect()
  customerSearch: Observable<Action> = this.actions.pipe(
    ofType<Search>(CustomerSearchActionType.Search),
    map(action => action.payload),
    switchMap(search =>
      this.customerService.search(search).pipe(
        map(customers => new SearchSuccess(customers)),
        catchError(error => of(new SearchFailed(error)))
      )
    )
  );
}
