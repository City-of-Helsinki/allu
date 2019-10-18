import {select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {Component, Input} from '@angular/core';
import {combineLatest, Observable} from 'rxjs';
import {decisionBlockedByReasons, DecisionBlockedReason} from '@feature/application/application-util';
import * as fromApplication from '@feature/application/reducers';
import {DECISION_BLOCKING_TAGS} from '@model/application/tag/application-tag-type';
import {map} from 'rxjs/operators';
import {translateArray} from '@util/translations';

@Component({
  selector: 'base-decision-actions',
  template: ''
})
export class BaseDecisionActionsComponent {

  @Input() hasInvoicing = false;

  decisionBlockedReasons$: Observable<string>;
  decisionBlocked$: Observable<boolean>;

  constructor(protected store: Store<fromRoot.State>) {}

  watchDecisionBlocked() {
    this.decisionBlockedReasons$ = this.decisionBlockedReasons().pipe(
      map((reasons: DecisionBlockedReason[]) => translateArray('decision.blockedBy', reasons)),
      map(reasons => reasons.join(' '))
    );
    this.decisionBlocked$ = this.decisionBlockedReasons().pipe(map(reasons => reasons.length > 0));
  }

  private decisionBlockedReasons(): Observable<DecisionBlockedReason[]> {
    return combineLatest([
      this.store.pipe(select(fromApplication.getCurrentApplication)),
      this.store.pipe(select(fromApplication.hasTags(DECISION_BLOCKING_TAGS)))
    ]).pipe(
      map(([app, hasBlockingTags]) => decisionBlockedByReasons(app, this.hasInvoicing, hasBlockingTags))
    );
  }
}
