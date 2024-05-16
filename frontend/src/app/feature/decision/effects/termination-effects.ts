import * as fromDecision from '@feature/decision/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Injectable} from '@angular/core';
import {Action, select, Store} from '@ngrx/store';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {TerminationService} from '@feature/decision/termination/termination-service';
import {from, Observable} from 'rxjs/index';
import {
  LoadDocument,
  LoadDocumentFailed,
  LoadDocumentSuccess,
  LoadInfoFailed,
  LoadInfoSuccess,
  MoveTerminationToDecision,
  MoveTerminationToDecisionFailed,
  MoveTerminationToDecisionSuccess,
  RemoveTerminationDraftSuccess,
  RemoveTerminationDraftFailure,
  Terminate,
  TerminationActionType,
  TerminationDraftFailed,
  TerminationDraftSuccess
} from '@feature/decision/actions/termination-actions';
import {catchError, filter, map, switchMap, tap, withLatestFrom} from 'rxjs/internal/operators';
import {NumberUtil} from '@util/number.util';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {Router} from '@angular/router';
import {DocumentActionType, SetTab} from '@feature/decision/actions/document-actions';
import {DecisionTab} from '@feature/decision/documents/decision-tab';
import {TerminationInfo} from '@feature/decision/termination/termination-info';
import {StatusChangeInfo} from '@model/application/status-change-info';
import {CommentType} from '@model/application/comment/comment-type';
import {ApplicationStore} from '@service/application/application-store';
import {ApplicationStatus} from '@model/application/application-status';
import {Application} from '@model/application/application';
import {withLatestExisting} from '@feature/common/with-latest-existing';

@Injectable()
export class TerminationEffects {
  constructor(private actions: Actions,
              private router: Router,
              private store: Store<fromDecision.State>,
              private terminationService: TerminationService,
              private applicationStore: ApplicationStore) {
  }

  
  loadTerminationInfo: Observable<Action> = createEffect(() => this.actions.pipe(
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
  ));

  
  loadTerminationDocument: Observable<Action> = createEffect(() => this.actions.pipe(
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
  ));

  
  terminationTabOpen: Observable<Action> = createEffect(() => this.actions.pipe(
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
  ));

  
  terminateDecision: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Terminate>(TerminationActionType.Terminate),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => {
      const draft: boolean = action.payload.draft;
      return this.terminationService.saveTerminationInfo(application.id, action.payload).pipe(
        switchMap((savedInfo) => {
          if (draft) {
            return from([new TerminationDraftSuccess(savedInfo), new LoadDocument()]);
          } else {
            return from([new TerminationDraftSuccess(savedInfo), new LoadDocument(), new MoveTerminationToDecision(savedInfo)]);
          }
        }),
        catchError(error => from([
          new TerminationDraftFailed(error),
          new NotifyFailure(error)
        ]))
      );
    })
  ));

  
  moveTerminationToDecision: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Terminate>(TerminationActionType.MoveTerminationToDecision),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.changeStatusToDecisionMaking(application.id, action.payload).pipe(
      tap( updatedApp => this.router.navigate(['/applications', updatedApp.id, 'summary', 'decision', 'termination'])),
      map( () => new MoveTerminationToDecisionSuccess()),
      catchError(error => from([
        new MoveTerminationToDecisionFailed(error),
        new NotifyFailure(error)
      ])),
    ))
  ));

  
  removeTerminationDraft: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Terminate>(TerminationActionType.RemoveTerminationDraft),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, application]) => {
      return this.terminationService.removeTerminationInfo(application.id).pipe(
        tap( () => this.router.navigate(['/applications', application.id, 'summary', 'decision'])),
        switchMap(() => {
          return from([new RemoveTerminationDraftSuccess()]);
        }),
        catchError(error => from([
          new RemoveTerminationDraftFailure(error),
          new NotifyFailure(error)
        ]))
      );
    })
  ));


  private changeStatusToDecisionMaking(applicationId: number, terminationInfo: TerminationInfo): Observable<Application> {
    const statusChangeInfo = this.asStatusChangeInfo(terminationInfo);
    return this.applicationStore.changeStatus(applicationId, ApplicationStatus.DECISIONMAKING, statusChangeInfo);
  }

  private asStatusChangeInfo(terminationInfo: TerminationInfo): StatusChangeInfo {
    return new StatusChangeInfo(
      CommentType.PROPOSE_TERMINATION,
      terminationInfo.comment,
      terminationInfo.owner
    );
  }
}
