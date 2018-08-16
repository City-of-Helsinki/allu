import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {Observable, of} from 'rxjs';

import {Application} from '../../model/application/application';
import {Some} from '../../util/option';
import {ApplicationStore} from '../../service/application/application-store';
import {NotificationService} from '../../service/notification/notification.service';
import {Store} from '@ngrx/store';
import * as fromApplication from './reducers';
import {Load, LoadSuccess} from './actions/application-actions';
import {ActionTargetType} from '../allu/actions/action-target-type';
import {catchError, filter, switchMap, take, tap} from 'rxjs/internal/operators';
import {Load as LoadTags, LoadSuccess as LoadTagsSuccess} from './actions/application-tag-actions';
import * as commentActions from '@feature/comment/actions/comment-actions';
import * as historyActions from '../history/actions/history-actions';
import * as metaActions from './actions/application-meta-actions';
import {NumberUtil} from '@util/number.util';

@Injectable()
export class ApplicationResolve implements Resolve<Application> {
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
    return this.store.select(fromApplication.getApplicationLoaded).pipe(
      filter(loaded => loaded),
      switchMap(() => this.store.select(fromApplication.getCurrentApplication)),
      tap(app => this.store.dispatch(new LoadSuccess(app))),
      tap(() => this.loadComments()),
      tap(() => this.store.dispatch(new historyActions.Load(ActionTargetType.Application))),
      tap(() => this.store.dispatch(new metaActions.Load())),
      take(1),
      catchError(err => this.handleError(err))
    );
  }

  private loadComments() {
    this.store.dispatch(new commentActions.Load(ActionTargetType.Application));
  }

  private handleError(err: any): Observable<Application> {
    this.notification.errorInfo(err);
    this.router.navigate(['/applications']);
    return of(new Application());
  }
}
