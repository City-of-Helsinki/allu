import * as fromDecision from '@feature/decision/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Injectable} from '@angular/core';
import {Action, Store} from '@ngrx/store';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {TerminationService} from '@feature/decision/termination/termination-service';
import {from, Observable} from 'rxjs/index';
import {
  TerminationActionType,
  Terminate, TerminationDraftSuccess, TerminationDraftFailed,
  MoveTerminationToDecision, MoveTerminationToDecisionSuccess, MoveTerminationToDecisionFailed, LoadSuccess, LoadFailed
} from '@feature/decision/actions/termination-actions';
import {catchError, filter, map, switchMap, withLatestFrom, tap} from 'rxjs/internal/operators';
import {NumberUtil} from '@util/number.util';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {Router} from '@angular/router';

@Injectable()
export class TerminationEffects {
  constructor(private actions: Actions,
              private router: Router,
              private store: Store<fromDecision.State>,
              private terminationService: TerminationService) {
  }

  @Effect()
  loadDecision: Observable<Action> = this.actions.pipe(
    ofType<Terminate>(TerminationActionType.Load),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => {
      return this.terminationService.getTerminationInfo(application.id).pipe(
        map((terminationInfo) => new LoadSuccess(terminationInfo)),
        catchError(error => from([
          new LoadFailed(error),
          new NotifyFailure(error)
        ]))
      );
    })
  );

  @Effect()
  terminateDecision: Observable<Action> = this.actions.pipe(
    ofType<Terminate>(TerminationActionType.Terminate),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => {
      const draft: boolean = action.payload.draft;
      return this.terminationService.saveTerminationInfo(application.id, action.payload).pipe(
        map((savedInfo) => {
          if (draft) {
            return new TerminationDraftSuccess(savedInfo);
          } else {
            return new MoveTerminationToDecision();
          }
        }),
        catchError(error => from([
          new TerminationDraftFailed(error),
          new NotifyFailure(error)
        ]))
      );
    })
  );

  @Effect()
  moveTerminationToDecision: Observable<Action> = this.actions.pipe(
    ofType<Terminate>(TerminationActionType.MoveTerminationToDecision),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.terminationService.moveTerminationToDecision(application.id).pipe(
      // TODO move to termination tab instead of decision tab after it's implemented
      tap( applicationId => this.router.navigate(['/applications', applicationId, 'summary', 'decision'])),
      map( () => new MoveTerminationToDecisionSuccess()),
      catchError(error => from([
        new MoveTerminationToDecisionFailed(error),
        new NotifyFailure(error)
      ])),
    ))
  );

}
