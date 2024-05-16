import {Injectable} from '@angular/core';
import * as fromApplication from '../../application/reducers';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {Action, Store} from '@ngrx/store';
import {ApplicationService} from '../../../service/application/application.service';
import {Observable, of} from 'rxjs/index';
import {
  Add, AddFailed, AddSuccess,
  ApplicationTagActionType,
  Load,
  LoadFailed,
  LoadSuccess,
  Remove, RemoveFailed, RemoveSuccess,
  Save,
  SaveFailed,
  SaveSuccess
} from '../actions/application-tag-actions';
import {catchError, filter, map, switchMap, withLatestFrom} from 'rxjs/internal/operators';
import {Application} from '../../../model/application/application';
import {NumberUtil} from '../../../util/number.util';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import * as historyActions from '@feature/history/actions/history-actions';

@Injectable()
export class ApplicationTagEffects {
  constructor(private actions: Actions,
              private store: Store<fromApplication.State>,
              private applicationService: ApplicationService) {
  }

  
  loadTags: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Load>(ApplicationTagActionType.Load),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    switchMap(([action, current]) => this.applicationService.getTags(current.id).pipe(
      map(tags => new LoadSuccess(tags)),
      catchError(error => of(new LoadFailed(error)))
    ))
  ));

  
  addTag: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Add>(ApplicationTagActionType.Add),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    switchMap(([action, application]) => {
      if (NumberUtil.isDefined(application.id)) {
        return this.applicationService.saveTag(application.id, action.payload).pipe(
          switchMap(saved => [
            new AddSuccess(saved),
            new historyActions.Load(ActionTargetType.Application)
          ]),
          catchError(error => of(new AddFailed(error)))
        );
      } else {
        return of(new AddSuccess(action.payload));
      }
    })
  ));

  
  removeTag: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Remove>(ApplicationTagActionType.Remove),
    withLatestFrom(this.store.select(fromApplication.getCurrentApplication)),
    switchMap(([action, application]) => {
      if (NumberUtil.isDefined(application.id)) {
        return this.applicationService.removeTag(application.id, action.payload).pipe(
          switchMap(() => [
            new RemoveSuccess(action.payload),
            new historyActions.Load(ActionTargetType.Application)
          ]),
          catchError(error => of(new RemoveFailed(error)))
        );
      } else {
        return of(new RemoveSuccess(action.payload));
      }
    })
  ));

  
  saveTags: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Save>(ApplicationTagActionType.Save),
    withLatestFrom(
      this.store.select(fromApplication.getTags),
      this.store.select(fromApplication.getCurrentApplication)
    ),
    filter(([action, tags, application]) => !!application && NumberUtil.isDefined(application.id)),
    switchMap(([action, tags, application]) => this.applicationService.saveTags(application.id, tags).pipe(
      map(saved => new SaveSuccess(saved)),
      catchError(error => of(new SaveFailed(error)))
    ))
  ));

}
