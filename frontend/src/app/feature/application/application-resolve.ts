import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, Resolve, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {Application} from '../../model/application/application';
import {Some} from '../../util/option';
import {ApplicationStore} from '../../service/application/application-store';
import {NotificationService} from '../../service/notification/notification.service';
import {Store} from '@ngrx/store';
import * as fromApplication from './reducers';
import {LoadSuccess} from './actions/application-actions';
import {Load} from '../comment/actions/comment-actions';
import {CommentTargetType} from '../../model/application/comment/comment-target-type';

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
      .map(id => this.applicationStore.load(id)
        .do(app => this.store.dispatch(new LoadSuccess(app)))
        .do(() => this.loadComments(id))
        .catch(err => this.handleError(err)))
      .orElseGet(() => Observable.of(this.applicationStore.currentOrCopy()));
  }

  private loadComments(id: number) {
    this.store.dispatch(new Load(CommentTargetType.Application));
  }

  private handleError(err: any): Observable<Application> {
    this.notification.errorInfo(err);
    this.router.navigate(['/applications']);
    return Observable.of(new Application());
  }
}
