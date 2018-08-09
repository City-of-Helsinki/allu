import {ActivatedRouteSnapshot, Resolve} from '@angular/router';
import * as fromDecision from '@feature/decision/reducers';
import {Store} from '@ngrx/store';
import {Injectable} from '@angular/core';
import {ShowActions} from '@feature/decision/actions/document-actions';

const DECISION = 'decision';

@Injectable()
export class DecisionResolve implements Resolve<boolean> {
  constructor(private store: Store<fromDecision.State>) {}

  resolve(route: ActivatedRouteSnapshot): boolean {
    const showActions = route.routeConfig.path === DECISION;
    this.store.dispatch(new ShowActions(showActions));
    return showActions;
  }
}
