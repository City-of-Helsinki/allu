import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {Action, select, Store} from '@ngrx/store';
import * as fromDecision from '@feature/decision/reducers';
import * as fromApplication from '@feature/application/reducers';
import {ApprovalDocumentService} from '@service/decision/approval-document.service';
import {Observable} from 'rxjs/internal/Observable';
import {catchError, filter, map, switchMap, withLatestFrom} from 'rxjs/operators';
import {DocumentActionType, SetTab} from '@feature/decision/actions/document-actions';
import {ApprovalDocumentActionType, Load, LoadFailed, LoadSuccess} from '@feature/decision/actions/approval-document.actions';
import {ApprovalDocumentType} from '@model/decision/approval-document';
import {NumberUtil} from '@util/number.util';
import {from} from 'rxjs/internal/observable/from';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {of} from 'rxjs/internal/observable/of';
import {EMPTY} from 'rxjs/internal/observable/empty';

const TabToDocumentType = {
  OPERATIONAL_CONDITION: ApprovalDocumentType.OPERATIONAL_CONDITION,
  WORK_FINISHED: ApprovalDocumentType.WORK_FINISHED
};

@Injectable()
export class ApprovalDocumentEffects {
  constructor(private actions: Actions,
              private store: Store<fromDecision.DecisionState>,
              private approvalDocumentService: ApprovalDocumentService) {
  }

  
  loadOperationalConditionApproval: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Load>(ApprovalDocumentActionType.Load),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.approvalDocumentService.fetch(application.id, action.documentType).pipe(
      map(document => new LoadSuccess(action.documentType, document)),
      catchError(error => from([
        new LoadFailed(action.documentType, error),
        new NotifyFailure(error)
      ]))
    ))
  ));

  
  tabOpen: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<SetTab>(DocumentActionType.SetTab),
    map(action => TabToDocumentType[action.payload]),
    filter(documentType => documentType !== undefined),
    this.getRelatedDocument(),
    map(([documentType, document]) => {
      if (document) {
        return new LoadSuccess(documentType, document);
      } else {
        return new Load(documentType);
      }
    })
  ));

  private getRelatedDocument() {
    return (source: Observable<ApprovalDocumentType>) => source.pipe(
      switchMap(documentType => {
        const documentTypeObs = of(documentType);
        if (documentType === ApprovalDocumentType.OPERATIONAL_CONDITION) {
          return documentTypeObs.pipe(withLatestFrom(this.store.pipe(select(fromDecision.getOperationalConditionApproval))));
        } else if (documentType === ApprovalDocumentType.WORK_FINISHED) {
          return documentTypeObs.pipe(withLatestFrom(this.store.pipe(select(fromDecision.getWorkFinishedApproval))));
        } else {
          return documentTypeObs.pipe(withLatestFrom(EMPTY));
        }
      })
    );
  }
}
