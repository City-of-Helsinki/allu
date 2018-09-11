import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Action, Store} from '@ngrx/store';
import {InvoiceService} from '../../../service/application/invoice/invoice.service';
import {Observable, of} from 'rxjs/index';
import {InvoicingActionType, SetRecipient, SetRecipientSuccess} from '../actions/invoicing-actions';
import * as fromApplication from '../reducers/index';
import * as TagAction from '../actions/application-tag-actions';
import {catchError, filter, switchMap, withLatestFrom} from 'rxjs/internal/operators';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';

@Injectable()
export class InvoicingEffects {
  constructor(private actions: Actions,
              private store: Store<fromApplication.State>,
              private invoiceService: InvoiceService) {}

  @Effect()
  setRecipient: Observable<Action> = this.actions.pipe(
    ofType<SetRecipient>(InvoicingActionType.SetRecipient),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, app]) => app.id !== undefined),
    switchMap(([action, app]) => this.invoiceService.saveRecipient(app.id, action.payload).pipe(
      switchMap(() => [new SetRecipientSuccess(action.payload), new TagAction.Load()]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );
}
