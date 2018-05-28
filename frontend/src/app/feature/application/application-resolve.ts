import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {Observable, of} from 'rxjs';

import {Application} from '../../model/application/application';
import {Some} from '../../util/option';
import {ApplicationStore} from '../../service/application/application-store';
import {NotificationService} from '../../service/notification/notification.service';
import {Store} from '@ngrx/store';
import * as fromApplication from './reducers';
import {LoadSuccess} from './actions/application-actions';
import {Load} from '../comment/actions/comment-actions';
import {ActionTargetType} from '../allu/actions/action-target-type';
import {catchError, tap} from 'rxjs/internal/operators';
import {Load as LoadTags, LoadSuccess as LoadTagsSuccess} from './actions/application-tag-actions';

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

    return Some(route.params['id'])
      .map(id => Number(id))
      .map(id => this.applicationStore.load(id).pipe(
        tap(app => this.store.dispatch(new LoadSuccess(app))),
        tap(() => this.loadComments()),
        tap(() => this.loadTags()),
        catchError(err => this.handleError(err)))
      )
      .orElseGet(() => this.currentOrCopy());
  }

  private loadComments() {
    this.store.dispatch(new Load(ActionTargetType.Application));
  }

  private loadTags() {
    this.store.dispatch(new LoadTags());
  }

  private handleError(err: any): Observable<Application> {
    this.notification.errorInfo(err);
    this.router.navigate(['/applications']);
    return of(new Application());
  }

  private currentOrCopy(): Observable<Application> {
    const app = this.applicationStore.currentOrCopy();
    this.store.dispatch(new LoadSuccess(app));
    this.store.dispatch(new LoadTagsSuccess([]));
    return of(app);
  }
}
