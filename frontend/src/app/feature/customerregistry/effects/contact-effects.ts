import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {from, Observable} from 'rxjs';
import {Action, select, Store} from '@ngrx/store';
import {catchError, filter, map, switchMap} from 'rxjs/operators';

import {ContactService} from '@service/customer/contact.service';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {ContactActionType, FindById, FindByIdComplete} from '@feature/customerregistry/actions/contact-actions';
import * as fromCustomerRegistry from '@feature/customerregistry/reducers';

@Injectable()
export class ContactEffects {
  constructor(private actions: Actions,
              private store: Store<fromCustomerRegistry.State>,
              private contactService: ContactService) {}

  
  getContact: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<FindById>(ContactActionType.FindById),
    switchMap(action => this.store.pipe(
      select(fromCustomerRegistry.getContact(action.payload)),
      filter(contact => !contact),
      switchMap(() => this.findById(action.payload))
    ))
  ));

  private findById(id: number) {
    return this.contactService.findById(id).pipe(
      map(contact => new FindByIdComplete({contact})),
      catchError(error => from([
        new NotifyFailure(error),
        new FindByIdComplete({contact: undefined, error})
      ])));
  }
}
