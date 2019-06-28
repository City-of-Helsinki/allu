import * as fromDecision from '@feature/decision/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Injectable} from '@angular/core';
import {Action, Store} from '@ngrx/store';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {TerminationService} from '@feature/decision/termination/termination-service';
import {from, of, Observable} from 'rxjs/index';
import {
  TerminationActionType,
  Terminate, TerminationDraftSuccess, TerminationDraftFailed,
  MoveTerminationToDecision, MoveTerminationToDecisionSuccess, MoveTerminationToDecisionFailed, LoadInfoSuccess,
  LoadInfoFailed, LoadDocument, LoadDocumentFailed, LoadDocumentSuccess
} from '@feature/decision/actions/termination-actions';
import {catchError, filter, map, switchMap, withLatestFrom, tap} from 'rxjs/internal/operators';
import {NumberUtil} from '@util/number.util';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {Router} from '@angular/router';
import {DocumentActionType, SetTab} from '@feature/decision/actions/document-actions';
import {DecisionTab} from '@feature/decision/documents/decision-tab';

@Injectable()
export class TerminationEffects {
  constructor(private actions: Actions,
              private router: Router,
              private store: Store<fromDecision.State>,
              private terminationService: TerminationService) {
  }

  @Effect()
  loadTerminationInfo: Observable<Action> = this.actions.pipe(
    ofType<Terminate>(TerminationActionType.LoadInfo),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => {
      return this.terminationService.getTerminationInfo(application.id).pipe(
        map((terminationInfo) => new LoadInfoSuccess(terminationInfo)),
        catchError(error => from([
          new LoadInfoFailed(error),
          new NotifyFailure(error)
        ]))
      );
    })
  );

  @Effect()
  loadTerminationDocument: Observable<Action> = this.actions.pipe(
    ofType<LoadDocument>(TerminationActionType.LoadDocument),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.terminationService.getTermination(application.id).pipe(
      map(response => new LoadDocumentSuccess(response)),
      catchError(error => from([
        new LoadDocumentFailed(error),
        new NotifyFailure(error)
      ]))
    ))
  );

  @Effect()
  terminationTabOpen: Observable<Action> = this.actions.pipe(
    ofType<SetTab>(DocumentActionType.SetTab),
    filter(action => action.payload === DecisionTab.TERMINATION),
    withLatestFrom(this.store.select(fromDecision.getTerminationDocument)),
    map(([action, termination]) => {
      if (termination) {
        return new LoadDocumentSuccess(termination);
      } else {
        return new LoadDocument();
      }
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
        switchMap((savedInfo) => {
          if (draft) {
            return of(new TerminationDraftSuccess(savedInfo));
          } else {
            return from([new TerminationDraftSuccess(savedInfo), new MoveTerminationToDecision()]);
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
      tap( applicationId => this.router.navigate(['/applications', applicationId, 'summary', 'decision', 'termination'])),
      map( () => new MoveTerminationToDecisionSuccess()),
      catchError(error => from([
        new MoveTerminationToDecisionFailed(error),
        new NotifyFailure(error)
      ])),
    ))
  );

}
