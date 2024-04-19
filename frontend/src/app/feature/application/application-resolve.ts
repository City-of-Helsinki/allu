import {Injectable} from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import {Observable, of} from 'rxjs';

import {Application} from '@model/application/application';
import {Some} from '@util/option';
import {ApplicationStore} from '@service/application/application-store';
import {NotificationService} from '@feature/notification/notification.service';
import {select, Store} from '@ngrx/store';
import * as fromApplication from './reducers';
import {Load, LoadDistribution, LoadSuccess, SaveDistributionSuccess} from './actions/application-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {catchError, filter, switchMap, take, tap} from 'rxjs/internal/operators';
import * as commentActions from '@feature/comment/actions/comment-actions';
import * as historyActions from '@feature/history/actions/history-actions';
import * as metaActions from './actions/application-meta-actions';
import * as informationRequestActions from '@feature/information-request/actions/information-request-actions';
import * as supervisionTaskActions from '@feature/application/supervision/actions/supervision-task-actions';
import * as invoicingCustomerActions from '@feature/application/invoicing/actions/invoicing-customer-actions';
import * as terminationActions from '@feature/decision/actions/termination-actions';
import * as summaryActions from '@feature/information-request/actions/information-request-summary-actions';
import {NumberUtil} from '@util/number.util';
import {ResetLayers} from '@feature/map/actions/map-layer-actions';

@Injectable()
export class ApplicationResolve  {
  constructor(private applicationStore: ApplicationStore,
              private store: Store<fromApplication.State>,
              private router: Router,
              private notification: NotificationService) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Application> {
    Some(route.queryParams)
      .map((params: {relatedProject}) => params.relatedProject)
      .do(relatedProject => this.applicationStore.changeRelatedProject(relatedProject));

    this.initApplication(route.params['id']);
    return this.waitForApplication();
  }

  private initApplication(id: number): void {
    if (NumberUtil.isDefined(id)) {
      this.store.dispatch(new Load(id));
    } else {
      const app = this.applicationStore.currentOrCopy();
      this.store.dispatch(new LoadSuccess(app));
    }
  }

  private waitForApplication(): Observable<Application> {
    return this.store.pipe(
      select(fromApplication.getApplicationLoaded),
      filter(loaded => loaded),
      switchMap(() => this.store.pipe(select(fromApplication.getCurrentApplication))),
      take(1),
      tap(app => this.store.dispatch(new LoadSuccess(app))),
      tap(app => this.loadRelatedInfo(app)),
      tap(app => this.store.dispatch(new ResetLayers(ActionTargetType.Location))),
      catchError(err => this.handleError(err))
    );
  }

  private loadRelatedInfo(app: Application): void {
    this.store.dispatch(new commentActions.Load(ActionTargetType.Application));
    this.store.dispatch(new historyActions.Load(ActionTargetType.Application));
    this.store.dispatch(new metaActions.Load());
    this.store.dispatch(new supervisionTaskActions.Load());
    this.store.dispatch(new invoicingCustomerActions.Load());
    this.store.dispatch(new informationRequestActions.LoadActiveRequest());
    this.store.dispatch(new terminationActions.LoadInfo());
    this.store.dispatch(new summaryActions.MarkForReload());
    this.store.dispatch(new LoadDistribution());
  }

  private handleError(err: any): Observable<Application> {
    this.notification.errorInfo(err);
    this.router.navigate(['/applications']);
    return of(new Application());
  }
}
