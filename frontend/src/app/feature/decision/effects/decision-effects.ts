import * as fromDecision from '@feature/decision/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Injectable} from '@angular/core';
import {Action, Store} from '@ngrx/store';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {DecisionService} from '@service/decision/decision.service';
import {from, Observable, of} from 'rxjs/index';
import {DecisionActionType, Load, LoadFailed, LoadSuccess} from '@feature/decision/actions/decision-actions';
import {catchError, filter, map, switchMap, withLatestFrom} from 'rxjs/internal/operators';
import {NumberUtil} from '@util/number.util';
import {DocumentActionType, SetTab} from '@feature/decision/actions/document-actions';
import {DecisionTab} from '@feature/decision/documents/decision-tab';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';

@Injectable()
export class DecisionEffects {
  constructor(private actions: Actions,
              private store: Store<fromDecision.State>,
              private decisionService: DecisionService) {
  }

  
  loadDecision: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Load>(DecisionActionType.Load),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.decisionService.fetch(application.id).pipe(
      map(response => new LoadSuccess(response)),
      catchError(error => from([
        new LoadFailed(error),
        new NotifyFailure(error)
      ]))
    ))
  ));

  
  decisionTabOpen: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<SetTab>(DocumentActionType.SetTab),
    filter(action => action.payload === DecisionTab.DECISION),
    withLatestFrom(this.store.select(fromDecision.getDecision)),
    map(([action, decision]) => {
      if (decision) {
        return new LoadSuccess(decision);
      } else {
        return new Load();
      }
    })
  ));
}
