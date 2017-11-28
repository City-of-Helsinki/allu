import {Injectable} from '@angular/core';
import {Resolve, ActivatedRouteSnapshot, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {Application} from '../../model/application/application';
import {Some} from '../../util/option';
import {ApplicationStore} from '../../service/application/application-store';
import {NotificationService} from '../../service/notification/notification.service';

@Injectable()
export class ApplicationResolve implements Resolve<Application> {
  constructor(private applicationStore: ApplicationStore, private router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Application> {
    Some(route.queryParams)
      .map((params: {relatedProject}) => params.relatedProject)
      .do(relatedProject => this.applicationStore.relatedProject = relatedProject);

    return Some(route.params['id'])
      .map(id => Number(id))
      .map(id => this.applicationStore.load(id)
        .do(app => this.loadComments(id))
        .catch(err => this.handleError(err)))
      .orElse(this.newOrCopy());
  }

  private loadComments(id: number) {
    this.applicationStore.loadComments(id).subscribe(
      comments => {},
      err => console.error('Failed to load comments for application', id, '.')
    );
  }

  private handleError(err: any): Observable<Application> {
    NotificationService.error(err);
    this.router.navigate(['/applications']);
    return Observable.of(new Application());
  }

  private newOrCopy(): Observable<Application> {
    if (this.applicationStore.isCopy) {
      this.applicationStore.isCopy = false;
    } else {
      this.applicationStore.reset();
    }
    return Observable.of(this.applicationStore.application);
  }
}
