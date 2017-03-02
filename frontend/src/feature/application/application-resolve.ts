import {Injectable} from '@angular/core';
import {Resolve, ActivatedRouteSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {Application} from '../../model/application/application';
import {Some} from '../../util/option';
import {ApplicationState} from '../../service/application/application-state';

@Injectable()
export class ApplicationResolve implements Resolve<Application> {
  constructor(private applicationState: ApplicationState) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Application> {
    Some(route.queryParams)
      .map((params: {relatedProject}) => params.relatedProject)
      .do(relatedProject => this.applicationState.relatedProject = relatedProject);

    return Some(route.params['id'])
      .map(id => Number(id))
      .map(id => this.applicationState.load(id)
        .do(app => this.loadComments(id)))
      .orElse(Observable.of(new Application()));
  }

  private loadComments(id: number) {
    this.applicationState.loadComments(id).subscribe(
      comments => {},
      err => console.error('Failed to load comments for application', id, '.')
    );
  }
}
