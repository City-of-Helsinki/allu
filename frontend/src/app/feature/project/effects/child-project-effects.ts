import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {ProjectService} from '../../../service/project/project.service';
import {Action, Store} from '@ngrx/store';
import * as fromProject from '../reducers';
import {from, Observable, of} from 'rxjs';
import {Add, AddSuccess, ChildProjectActionType, Load, LoadFailed, LoadSuccess} from '../actions/child-project-actions';
import {catchError, map, switchMap, withLatestFrom} from 'rxjs/operators';
import {NumberUtil} from '../../../util/number.util';
import {filter} from 'rxjs/internal/operators';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';

@Injectable()
export class ChildProjectEffects {
  constructor(private actions: Actions,
              private store: Store<fromProject.State>,
              private projectService: ProjectService) {}

  
  loadChildren: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Load>(ChildProjectActionType.Load),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    filter(([payload, project]) => NumberUtil.isExisting(project)),
    switchMap(([payload, project]) =>
      this.projectService.getChildProjects(project.id)
        .pipe(
          map(children => new LoadSuccess(children)),
          catchError(error => from([
            new LoadFailed(error),
            new NotifyFailure(error)
          ]))
        )
    )
  ));

  
  addChild: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Add>(ChildProjectActionType.Add),
    withLatestFrom(this.store.select(fromProject.getCurrentProject)),
    switchMap(([action, project]) =>
      this.projectService.updateParent(action.payload, project.id).pipe(
        map(updated => new AddSuccess(updated)),
        catchError(error => of(new NotifyFailure(error)))
      )
    )
  ));
}
