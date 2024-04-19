import {Injectable} from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';
import {Observable} from 'rxjs';

import {Project} from '../../model/project/project';
import {Some} from '../../util/option';
import {ProjectState} from '../../service/project/project-state';
import {Store} from '@ngrx/store';
import * as fromProject from './reducers';
import * as projectActions from './actions/project-actions';
import * as childActions from './actions/child-project-actions';
import * as parentActions from './actions/parent-project-actions';
import * as applicationActions from './actions/application-actions';
import * as commentActions from '../comment/actions/comment-actions';
import * as historyActions from '../history/actions/history-actions';
import {NumberUtil} from '../../util/number.util';

import {ActionTargetType} from '../allu/actions/action-target-type';
import {filter, switchMap, take, tap} from 'rxjs/internal/operators';
import {ResetLayers} from '@feature/map/actions/map-layer-actions';

@Injectable()
export class ProjectResolve  {
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
    return this.store.select(fromProject.getProjectLoaded).pipe(
      filter(loaded => loaded),
      switchMap(() => this.store.select(fromProject.getCurrentProject)),
      take(1),
      tap(() => this.store.dispatch(new commentActions.Load(ActionTargetType.Project))),
      tap(() => this.store.dispatch(new historyActions.Load(ActionTargetType.Project))),
      tap(() => this.store.dispatch(new childActions.Load())),
      tap(() => this.store.dispatch(new parentActions.Load())),
      tap(() => this.store.dispatch(new applicationActions.Load())),
      tap(() => this.store.dispatch(new ResetLayers(ActionTargetType.Location)))
    );
  }
}
