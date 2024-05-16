import {Injectable} from '@angular/core';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import * as ChargeBasisAction from '@feature/application/invoicing/actions/charge-basis-actions';
import {ChargeBasisActionType} from '@feature/application/invoicing/actions/charge-basis-actions';
import {Action, Store} from '@ngrx/store';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {Observable, of} from 'rxjs/index';
import {withLatestExisting} from '@feature/common/with-latest-existing';
import {catchError, map, switchMap} from 'rxjs/internal/operators';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {InvoiceService} from '@service/application/invoice/invoice.service';
import {InvoiceActionType, Load, LoadSuccess} from '@feature/application/invoicing/actions/invoice-actions';

@Injectable()
export class InvoiceEffects {
  constructor(private actions: Actions,
              private store: Store<fromRoot.State>,
              private invoiceService: InvoiceService) {}

  
  load: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Load>(InvoiceActionType.Load),
    withLatestExisting(this.store.select(fromApplication.getCurrentApplication)),
    switchMap(([action, app]) => this.invoiceService.getInvoices(app.id).pipe(
      map(invoices => new LoadSuccess(invoices)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  onChargeBasisLoaded: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ChargeBasisAction.LoadSuccess>(ChargeBasisActionType.LoadSuccess),
    map(() => new Load())
  ));
}
