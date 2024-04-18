import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {ProjectService} from '../../../service/project/project.service';
import {Action, Store} from '@ngrx/store';
import * as fromProject from '../reducers';
import {from, Observable, of} from 'rxjs';
import {ParentProjectActionType, Load, LoadFailed, LoadSuccess} from '../actions/parent-project-actions';
import {catchError, map, switchMap, withLatestFrom} from 'rxjs/operators';
import {filter} from 'rxjs/internal/operators';
import {NumberUtil} from '../../../util/number.util';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';

@Injectable()
export class ParentProjectEffects {
  constructor(private actions: Actions,
              private store: Store<fromProject.State>,
              private projectService: ProjectService) {}

  
  loadParents: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Load>(ParentProjectActionType.Load),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    filter(([payload, project]) => NumberUtil.isExisting(project)),
    switchMap(([payload, project]) =>
      this.projectService.getParentProjects(project.id)
        .pipe(
          map(parents => new LoadSuccess(parents)),
          catchError(error => from([
            new LoadFailed(error),
            new NotifyFailure(error)
          ]))
        )
    )
  ));
}
