import {Injectable} from '@angular/core';
import {DecisionService} from '@app/service/decision/decision.service';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Store, Action} from '@ngrx/store';
import * as fromDecision from '@feature/decision/reducers';
import {Observable, from} from 'rxjs';
import {Load, BulkApprovalActionType, LoadComplete} from '@feature/decision/actions/bulk-approval-actions';
import {switchMap, map, catchError} from 'rxjs/operators';
import {NotifyFailure} from '@app/feature/notification/actions/notification-actions';

@Injectable()
export class BulkApprovalEffects {
  constructor(
    private actions: Actions,
    private store: Store<fromDecision.State>,
    private decisionService: DecisionService) {}

  @Effect()
  loadBulkApprovalEntries: Observable<Action> = this.actions.pipe(
    ofType<Load>(BulkApprovalActionType.Load),
    switchMap(action => this.decisionService.getBulkApprovalEntries(action.payload).pipe(
      map(entries => new LoadComplete({entries})),
      catchError(error => from([
        new LoadComplete({entries: [], error: error}),
        new NotifyFailure(error)
      ]))
    ))
  );
}
