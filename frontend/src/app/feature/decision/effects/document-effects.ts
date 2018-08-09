import {map} from 'rxjs/internal/operators';
import {Action} from '@ngrx/store';
import {Observable} from 'rxjs/index';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Injectable} from '@angular/core';
import {DocumentActionType, SetTab} from '@feature/decision/actions/document-actions';
import {DecisionTab} from '@feature/decision/documents/decision-tab';
import * as DecisionAction from '@feature/decision/actions/decision-actions';
import * as ContractAction from '@feature/decision/actions/contract-actions';

@Injectable()
export class DocumentEffects {
  constructor(private actions: Actions) {
  }

  @Effect()
  loadDocument: Observable<Action> = this.actions.pipe(
    ofType<SetTab>(DocumentActionType.SetTab),
    map(action => action.payload),
    map((tab: DecisionTab) => {
      return tab === DecisionTab.DECISION
        ? new DecisionAction.Load()
        : new ContractAction.Load();
    })
  );
}
