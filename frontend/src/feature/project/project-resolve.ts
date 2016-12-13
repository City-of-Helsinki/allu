import {Injectable} from '@angular/core';
import {Resolve, ActivatedRouteSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import '../../rxjs-extensions.ts';

import {Project} from '../../model/project/project';
import {ProjectHub} from '../../service/project/project-hub';
import {Some} from '../../util/option';

@Injectable()
export class ProjectResolve implements Resolve<Project> {
  constructor(private projectHub: ProjectHub) {}

  resolve(route: ActivatedRouteSnapshot): Observable<Project> {
    return Some(route.params['id'])
      .map(id => Number(id))
      .map(id => this.projectHub.getProject(id))
      .orElse(Observable.of(new Project()));
  }
}
