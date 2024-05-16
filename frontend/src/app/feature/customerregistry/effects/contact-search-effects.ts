import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {Observable, of} from 'rxjs';
import {Action} from '@ngrx/store';
import {catchError, concatMap, map, switchMap} from 'rxjs/operators';
import {CustomerService} from '@service/customer/customer.service';
import {
  ContactSearchActionType,
  LoadByCustomer,
  LoadByCustomerFailed,
  LoadByCustomerSuccess,
  Search,
  SearchSuccess
} from '../actions/contact-search-actions';
import {ContactService} from '@service/customer/contact.service';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';

@Injectable()
export class ContactSearchEffects {
  constructor(private actions: Actions,
              private customerService: CustomerService,
              private contactService: ContactService) {}

  
  loadContacts: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<LoadByCustomer>(ContactSearchActionType.LoadByCustomer),
    concatMap(action =>
      this.customerService.findCustomerContacts(action.payload).pipe(
        map(contacts => new LoadByCustomerSuccess(action.targetType, contacts)),
        catchError(error => of(new LoadByCustomerFailed(action.targetType, error)))
      )
    )
  ));

  
  search: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Search>(ContactSearchActionType.Search),
    switchMap(action => this.contactService.search(action.payload.query, action.payload.sort, action.payload.pageRequest).pipe(
      map(contacts => new SearchSuccess(action.targetType, contacts)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));
}
