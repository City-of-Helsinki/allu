import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {ApplicationService} from '../../../service/application/application.service';
import {Observable} from 'rxjs/Observable';
import {Action} from '@ngrx/store';
import * as application from '../actions/application-search-actions';
import * as customer from '../actions/customer-search-actions';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import {of} from 'rxjs/observable/of';
import {CustomerService} from '../../../service/customer/customer.service';
import {CustomerSearchActionType} from '../actions/customer-search-actions';
import {ApplicationSearchActionType} from '../actions/application-search-actions';

@Injectable()
export class SearchEffects {
  constructor(private actions: Actions,
              private applicationService: ApplicationService,
              private customerService: CustomerService) {}

  @Effect()
  applicationSearch: Observable<Action> = this.actions.pipe(
    ofType<application.Search>(ApplicationSearchActionType.Search),
    map(action => action.payload),
    filter(term => term && term.length > 2),
    switchMap(searchTerm =>
      this.applicationService.freeTextSearch(searchTerm).pipe(
        map(applications => new application.SearchSuccess(applications)),
        catchError(error => of(new application.SearchFailed(error)))
      )
    )
  );

  @Effect()
  customerSearch: Observable<Action> = this.actions.pipe(
    ofType<customer.Search>(CustomerSearchActionType.Search),
    map(action => action.payload),
    switchMap(search =>
      this.customerService.search(search).pipe(
        map(customers => new customer.SearchSuccess(customers)),
        catchError(error => of(new customer.SearchFailed(error)))
      )
    )
  );

  @Effect()
  loadContacts: Observable<Action> = this.actions.pipe(
    ofType<customer.LoadContacts>(CustomerSearchActionType.LoadContacts),
    map(action => action.payload),
    switchMap(customerId =>
      this.customerService.findCustomerContacts(customerId).pipe(
        map(contacts => new customer.LoadContactsSuccess(contacts)),
        catchError(error => of(new customer.LoadContactsFailed(error)))
      )
    )
  );
}
