import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Observable} from 'rxjs/internal/Observable';
import {Action, select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromInvoicing from '@feature/application/invoicing/reducers';
import * as fromApplication from '@feature/application/reducers';
import {
  Change,
  InvoicingPeriodActionType,
  Load,
  LoadSuccess,
  OperationFailed, Remove, RemoveSuccess
} from '@feature/application/invoicing/actions/invoicing-period-actions';
import {withLatestExisting} from '@feature/common/with-latest-existing';
import {catchError, map, switchMap, withLatestFrom} from 'rxjs/operators';
import {InvoicingPeriodService} from '@feature/application/invoicing/invoicing-period/invoicing-period.service';
import {from} from 'rxjs/internal/observable/from';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';

@Injectable()
export class InvoicingPeriodEffects {
  constructor(private actions: Actions,
              private store: Store<fromRoot.State>,
              private invoicingPeriodService: InvoicingPeriodService) {}

  @Effect()
  load: Observable<Action> = this.actions.pipe(
    ofType<Load>(InvoicingPeriodActionType.Load),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) => this.invoicingPeriodService.load(app.id).pipe(
      map(periods => new LoadSuccess(periods)),
      catchError(error => from([
        new NotifyFailure(error),
        new OperationFailed()
      ]))
    ))
  );

  @Effect()
  change: Observable<Action> = this.actions.pipe(
    ofType<Change>(InvoicingPeriodActionType.Change),
    withLatestFrom(
      this.store.pipe(select(fromApplication.getCurrentApplication)),
      this.store.pipe(select(fromInvoicing.getPeriodTotal)),
    ),
    switchMap(([action, app, periodCount]) => {
      if (periodCount) {
        return this.invoicingPeriodService.update(app.id, action.payload);
      } else {
        return this.invoicingPeriodService.create(app.id, action.payload);
      }
    }),
    map(periods => new LoadSuccess(periods)),
    catchError(error => from([
      new NotifyFailure(error),
      new OperationFailed()
    ]))
  );

  @Effect()
  remove: Observable<Action> = this.actions.pipe(
    ofType<Remove>(InvoicingPeriodActionType.Remove),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) => this.invoicingPeriodService.remove(app.id).pipe(
      map(periods => new RemoveSuccess()),
      catchError(error => from([
        new NotifyFailure(error),
        new OperationFailed()
      ]))
    ))
  );
}
