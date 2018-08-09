import * as fromDecision from '@feature/decision/reducers/decision-reducer';
import * as fromApplication from '@feature/application/reducers';
import {Injectable} from '@angular/core';
import {Action, Store} from '@ngrx/store';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {DecisionService} from '@service/decision/decision.service';
import {Observable, of} from 'rxjs/index';
import {DecisionActionType, Load, LoadFailed, LoadSuccess} from '@feature/decision/actions/decision-actions';
import {catchError, filter, map, switchMap, withLatestFrom} from 'rxjs/internal/operators';
import {NumberUtil} from '@util/number.util';

@Injectable()
export class DecisionEffects {
  constructor(private actions: Actions,
              private store: Store<fromDecision.State>,
              private decisionService: DecisionService) {
  }

  @Effect()
  loadDecision: Observable<Action> = this.actions.pipe(
    ofType<Load>(DecisionActionType.Load),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    filter(([action, application]) => NumberUtil.isExisting(application)),
    switchMap(([action, application]) => this.decisionService.fetch(application.id).pipe(
      map(response => new LoadSuccess(response)),
      catchError(error => of(new LoadFailed(error)))
    ))
  );
}
