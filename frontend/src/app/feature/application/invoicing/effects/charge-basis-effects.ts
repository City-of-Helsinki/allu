import {Injectable} from '@angular/core';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import * as fromInvoicing from '@feature/application/invoicing/reducers';
import {Action, select, Store} from '@ngrx/store';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {from, Observable, of} from 'rxjs/index';
import {
  ChargeBasisActionType,
  Load,
  LoadSuccess,
  Save,
  SetInvoicable,
  SetInvoicableFailed, UpdateEntry
} from '@feature/application/invoicing/actions/charge-basis-actions';
import {withLatestExisting} from '@feature/common/with-latest-existing';
import {InvoiceService} from '@service/application/invoice/invoice.service';
import {catchError, map, switchMap, withLatestFrom} from 'rxjs/internal/operators';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import * as ApplicationActions from '@feature/application/actions/application-actions';
import {
  InvoicingPeriodSuccessActions,
  invoicingPeriodSuccessActionTypes
} from '@feature/application/invoicing/actions/invoicing-period-actions';

@Injectable()
export class ChargeBasisEffects {
  constructor(private actions: Actions,
              private store: Store<fromRoot.State>,
              private invoiceService: InvoiceService) {}

  @Effect()
  load: Observable<Action> = this.actions.pipe(
    ofType<Load>(ChargeBasisActionType.Load),
    withLatestExisting(this.store.select(fromApplication.getCurrentApplication)),
    switchMap(([action, app]) => this.invoiceService.getChargeBasisEntries(app.id).pipe(
      map(entries => new LoadSuccess(entries)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  save: Observable<Action> = this.actions.pipe(
    ofType<Save>(ChargeBasisActionType.Save),
    withLatestExisting(this.store.select(fromApplication.getCurrentApplication)),
    switchMap(([action, app]) => this.invoiceService.saveChargeBasisEntries(app.id, action.payload).pipe(
      switchMap(entries => [new LoadSuccess(entries), new ApplicationActions.Load(app.id)]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  setInvoicable: Observable<Action> = this.actions.pipe(
    ofType<SetInvoicable>(ChargeBasisActionType.SetInvoicable),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) => this.invoiceService.setInvoicable(app.id, action.payload.id, action.payload.invoicable).pipe(
      map(entry => new UpdateEntry(entry)),
      catchError(error => from([new NotifyFailure(error), new SetInvoicableFailed(action.payload.id, action.payload.invoicable)]))
    ))
  );

  @Effect()
  onChanges: Observable<Action> = this.actions.pipe(
    ofType(ChargeBasisActionType.AddEntry, ChargeBasisActionType.UpdateEntry, ChargeBasisActionType.RemoveEntry),
    withLatestFrom(this.store.select(fromInvoicing.getAllChargeBasisEntries)),
    map(([action, entries]) => new Save(entries))
  );

  @Effect()
  onInvoicingPeriodChange: Observable<Action> = this.actions.pipe(
    ofType<InvoicingPeriodSuccessActions>(...invoicingPeriodSuccessActionTypes),
    map(() => new Load())
  );
}
