import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import {ProjectService} from '../../../service/project/project.service';
import {Action, Store} from '@ngrx/store';
import * as fromProject from '../reducers';
import {Observable, of} from 'rxjs';
import {ParentProjectActionType, Load, LoadFailed, LoadSuccess} from '../actions/parent-project-actions';
import {catchError, map, switchMap, withLatestFrom} from 'rxjs/operators';
import {filter} from 'rxjs/internal/operators';
import {NumberUtil} from '../../../util/number.util';

@Injectable()
export class ParentProjectEffects {
  constructor(private actions: Actions,
              private store: Store<fromProject.State>,
              private projectService: ProjectService) {}

  @Effect()
  loadParents: Observable<Action> = this.actions.pipe(
    ofType<Load>(ParentProjectActionType.Load),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    filter(([payload, project]) => NumberUtil.isExisting(project)),
    switchMap(([payload, project]) =>
      this.projectService.getParentProjects(project.id)
        .pipe(
          map(parents => new LoadSuccess(parents)),
          catchError(error => of(new LoadFailed(error)))
        )
    )
  );
}
