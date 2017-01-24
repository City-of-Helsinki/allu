import {Injectable} from '@angular/core';
import {Resolve, ActivatedRouteSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {Project} from '../../model/project/project';
import {Some} from '../../util/option';
import {ProjectState} from '../../service/project/project-state';

@Injectable()
export class ProjectResolve implements Resolve<Project> {
  constructor(private projectState: ProjectState) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Project> {
    let projectId = Some(route.params['id']).orElse(route.parent.params['id']);

    return Some(projectId)
      .map(id => Number(id))
      .map(id => this.projectState.load(id)
        .do(project => this.loadRelated(id)))
      .orElse(this.projectState.createNew());
  }

  private loadRelated(id: number) {
    // Need to subscribe because otherwise data is not loaded
    this.projectState.loadRelatedProjects(id).subscribe(related => {});
    this.projectState.loadApplications(id).subscribe(apps => {});
  }
}
