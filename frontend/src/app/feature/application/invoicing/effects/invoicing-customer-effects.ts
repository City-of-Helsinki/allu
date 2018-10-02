import {Injectable} from '@angular/core';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Action, Store} from '@ngrx/store';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Observable, of} from 'rxjs/index';
import {withLatestExisting} from '@feature/common/with-latest-existing';
import {catchError, filter, map, switchMap, withLatestFrom} from 'rxjs/internal/operators';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {CustomerService} from '@service/customer/customer.service';
import {
  InvoicingCustomerActionType,
  Load,
  LoadSuccess,
  SetRecipient, SetRecipientSuccess
} from '@feature/application/invoicing/actions/invoicing-customer-actions';
import {NumberUtil} from '@util/number.util';
import * as TagAction from '@feature/application/actions/application-tag-actions';
import {InvoiceService} from '@service/application/invoice/invoice.service';

@Injectable()
export class InvoicingCustomerEffects {
  constructor(private actions: Actions,
              private store: Store<fromRoot.State>,
              private customerService: CustomerService,
              private invoiceService: InvoiceService) {}

  @Effect()
  load: Observable<Action> = this.actions.pipe(
    ofType<Load>(InvoicingCustomerActionType.Load),
    withLatestExisting<Load>(this.store.select(fromApplication.getCurrentApplication)),
    switchMap(([action, app]) => this.findInvoicingCustomer(app.invoiceRecipientId))
  );

  @Effect()
  setRecipient: Observable<Action> = this.actions.pipe(
    ofType<SetRecipient>(InvoicingCustomerActionType.SetRecipient),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, app]) => app.id !== undefined),
    switchMap(([action, app]) => this.invoiceService.saveRecipient(app.id, action.payload).pipe(
      switchMap(() => [new SetRecipientSuccess(action.payload), new TagAction.Load(), new Load()]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  findInvoicingCustomer(id: number): Observable<Action> {
    if (NumberUtil.isDefined(id)) {
      return this.customerService.findCustomerById(id).pipe(
        map(customer => new LoadSuccess(customer)),
        catchError(error => of(new NotifyFailure(error)))
      );
    } else {
      return of(new LoadSuccess(undefined));
    }
  }
}
