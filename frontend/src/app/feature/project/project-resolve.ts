import {Injectable} from '@angular/core';
import {Resolve, ActivatedRouteSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {Project} from '../../model/project/project';
import {Some} from '../../util/option';
import {ProjectState} from '../../service/project/project-state';
import {Store} from '@ngrx/store';
import * as fromProject from './reducers';
import * as projectActions from './actions/project-actions';
import {NumberUtil} from '../../util/number.util';
import 'rxjs/add/operator/skipWhile';

@Injectable()
export class ProjectResolve implements Resolve<Project> {
  constructor(private projectState: ProjectState, private store: Store<fromProject.State>) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Project> {
    const projectId = Some(route.params['id']).orElse(route.parent.params['id']);
    this.initProject(projectId);
    return this.waitForProject();
  }

  private initProject(projectId: number): void {
    if (NumberUtil.isDefined(projectId)) {
      this.store.dispatch(new projectActions.Load(projectId));
    } else {
      this.store.dispatch(new projectActions.LoadSuccess(new Project()));
    }
  }

  private waitForProject(): Observable<Project> {
    return this.store.select(fromProject.getProjectLoaded)
      .filter(loaded => loaded)
      .switchMap(() => this.store.select(fromProject.getCurrentProject))
      .take(1);
  }
}
