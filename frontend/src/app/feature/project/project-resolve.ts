import {Injectable} from '@angular/core';
import {Resolve, ActivatedRouteSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';

import {Project} from '../../model/project/project';
import {Some} from '../../util/option';
import {ProjectState} from '../../service/project/project-state';
import {Store} from '@ngrx/store';
import * as fromProject from './reducers';
import * as projectActions from './actions/project-actions';

@Injectable()
export class ProjectResolve implements Resolve<Project> {
  constructor(private projectState: ProjectState, private store: Store<fromProject.State>) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Project> {
    const projectId = Some(route.params['id']).orElse(route.parent.params['id']);

    return Some(projectId)
      .map(id => Number(id))
      .map(id => this.projectState.load(id)
        .do(project => {
          this.store.dispatch(new projectActions.LoadSuccess(project));
          this.loadRelated(id);
        }))
      .orElse(this.projectState.createNew());
  }

  private loadRelated(id: number) {
    // Need to subscribe because otherwise data is not loaded
    this.projectState.loadRelatedProjects(id).subscribe(related => {});
    this.projectState.loadApplications(id).subscribe(apps => {});
  }
}
