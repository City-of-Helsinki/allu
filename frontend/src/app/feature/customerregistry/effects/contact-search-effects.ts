import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Observable, of} from 'rxjs';
import {Action} from '@ngrx/store';
import {catchError, map, switchMap} from 'rxjs/operators';
import {CustomerService} from '../../../service/customer/customer.service';
import {
  ContactSearchActionType,
  LoadByCustomer,
  LoadByCustomerSuccess,
  LoadByCustomerFailed
} from '../actions/contact-search-actions';

@Injectable()
export class ContactSearchEffects {
  constructor(private actions: Actions,
              private customerService: CustomerService) {}

  @Effect()
  loadContacts: Observable<Action> = this.actions.pipe(
    ofType<LoadByCustomer>(ContactSearchActionType.LoadByCustomer),
    switchMap(action =>
      this.customerService.findCustomerContacts(action.payload).pipe(
        map(contacts => new LoadByCustomerSuccess(action.targetType, contacts)),
        catchError(error => of(new LoadByCustomerFailed(action.targetType, error)))
      )
    )
  );
}
