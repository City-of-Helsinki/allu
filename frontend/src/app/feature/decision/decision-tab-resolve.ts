import { ActivatedRouteSnapshot } from '@angular/router';
import * as fromDecision from '@feature/decision/reducers';
import {Store} from '@ngrx/store';
import {Injectable} from '@angular/core';
import {SetTab} from '@feature/decision/actions/document-actions';
import {DecisionTab} from '@feature/decision/documents/decision-tab';


@Injectable()
export class DecisionTabResolve  {
  constructor(private store: Store<fromDecision.State>) {}

  resolve(route: ActivatedRouteSnapshot): boolean {
    const tab = <DecisionTab>route.routeConfig.path.toUpperCase();
    this.store.dispatch(new SetTab(tab));
    return true;
  }
}
