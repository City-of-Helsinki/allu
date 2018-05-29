import {catchError, filter, map, switchMap, tap, withLatestFrom} from 'rxjs/operators';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {Injectable} from '@angular/core';
import * as fromProject from '../reducers';
import {Observable, of} from 'rxjs';
import {Action, Store} from '@ngrx/store';
import {ProjectService} from '../../../service/project/project.service';
import {
  Load,
  LoadFailed,
  LoadSuccess,
  ProjectActionTypes,
  Save,
  SaveFailed,
  SaveSuccess,
  Delete,
  DeleteFailed,
  DeleteSuccess
} from '../actions/project-actions';
import * as MetaAction from '../actions/project-meta-actions';
import {Router} from '@angular/router';
import { NumberUtil } from '../../../util/number.util';
import {META_PROJECT, MetadataService} from '../../../service/meta/metadata.service';
import {defer} from 'rxjs/index';
import * as fromAuth from '../../auth/reducers';

@Injectable()
export class ProjectEffects {

  constructor(private actions: Actions,
              private store: Store<fromProject.State>,
              private projectService: ProjectService,
              private metadataService: MetadataService,
              private router: Router) {
  }

  @Effect()
  loadApplications: Observable<Action> = this.actions.pipe(
    ofType<Load>(ProjectActionTypes.Load),
    map(action => action.payload),
    switchMap(payload =>
      this.projectService.getProject(payload)
        .pipe(
          map(applications => new LoadSuccess(applications)),
          catchError(error => of(new LoadFailed(error)))
        )
    )
  );

  @Effect()
  save: Observable<Action> = this.actions.pipe(
    ofType<Save>(ProjectActionTypes.Save),
    map(action => action.payload),
    switchMap(project =>
      this.projectService.save(project).pipe(
        map(saved => new SaveSuccess(saved)),
        catchError(error => of(new SaveFailed(error)))
      )
    )
  );

  @Effect()
  delete: Observable<Action> = this.actions.pipe(
    ofType<Delete>(ProjectActionTypes.Delete),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    filter(([action, project]) => NumberUtil.isExisting(project)),
    switchMap(([action, project])  =>
      this.projectService.delete(project.id).pipe(
        map(() => new DeleteSuccess()),
        catchError(error => of(new DeleteFailed(error)))
      )
    )
  );

  @Effect({dispatch: false})
  navigateAfterSave = this.actions.pipe(
    ofType<SaveSuccess>(ProjectActionTypes.SaveSuccess),
    map(action => action.payload),
    tap(project => this.router.navigate(['/projects', project.id]))
  );

  @Effect({dispatch: false})
  navigateAfterDelete = this.actions.pipe(
    ofType<DeleteSuccess>(ProjectActionTypes.DeleteSuccess),
    tap(project => this.router.navigate(['/projects']))
  );

  /**
   * Load project meta after logged in
   */
  @Effect()
  loadMeta: Observable<Action> = defer(() => this.store.select(fromAuth.getLoggedIn).pipe(
    filter(loggedIn => loggedIn),
    switchMap(() => this.metadataService.loadByType(META_PROJECT).pipe(
      map(meta => new MetaAction.LoadSuccess(meta)),
      catchError(error => of(new MetaAction.LoadFailed(error)))
    ))
  ));
}
