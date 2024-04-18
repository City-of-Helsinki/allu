import {Injectable} from '@angular/core';
import {DecisionService} from '@app/service/decision/decision.service';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {Action, Store} from '@ngrx/store';
import * as fromDecision from '@feature/decision/reducers';
import {forkJoin, from, Observable, of} from 'rxjs';
import {
  Approve,
  ApproveComplete,
  ApproveEntryComplete,
  BulkApprovalActionType,
  Load,
  LoadComplete
} from '@feature/decision/actions/bulk-approval-actions';
import {catchError, map, mergeMap, switchMap, tap} from 'rxjs/operators';
import {NotifyFailure} from '@app/feature/notification/actions/notification-actions';
import {BulkApprovalEntry} from '@model/decision/bulk-approval-entry';
import {ApplicationService} from '@service/application/application.service';
import {StatusChangeInfo} from '@model/application/status-change-info';
import {DecisionDetails} from '@model/decision/decision-details';

@Injectable()
export class BulkApprovalEffects {
  constructor(
    private actions: Actions,
    private store: Store<fromDecision.State>,
    private decisionService: DecisionService,
    private applicationService: ApplicationService) {}

  
  loadBulkApprovalEntries: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Load>(BulkApprovalActionType.Load),
    switchMap(action => this.decisionService.getBulkApprovalEntries(action.payload).pipe(
      map(entries => new LoadComplete({entries})),
      catchError(error => from([
        new LoadComplete({entries: [], error: error}),
        new NotifyFailure(error)
      ]))
    ))
  ));

  
  approve: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Approve>(BulkApprovalActionType.Approve),
    mergeMap(action => forkJoin(...action.payload.map(entry => this.approveEntry(entry)))),
    map(() => new ApproveComplete())
  ));

  approveEntry(entry: BulkApprovalEntry): Observable<ApproveEntryComplete> {
    return this.applicationService.changeStatus(entry.id, entry.targetState, new StatusChangeInfo()).pipe(
      switchMap(app => this.decisionService.sendByStatus(entry.id, entry.targetState, new DecisionDetails(entry.distributionList)).pipe(
        map(result => new ApproveEntryComplete({id: entry.id})),
        catchError(error => of(new ApproveEntryComplete({id: entry.id, error: error}))),
        tap(action => this.store.dispatch(action)) // Dispatch action to notify progress
      ))
    );
  }
}
