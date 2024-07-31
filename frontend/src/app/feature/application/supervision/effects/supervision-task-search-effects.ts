import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {Action, select, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import {SupervisionTaskService} from '@service/supervision/supervision-task.service';
import {combineLatest, Observable, of} from 'rxjs';
import {
  ClearSelected,
  Search, SearchFailed,
  SearchSuccess,
  SupervisionTaskSearchActionType
} from '@feature/application/supervision/actions/supervision-task-search-actions';
import {catchError, filter, map, switchMap} from 'rxjs/operators';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {Sort} from '@model/common/sort';
import {PageRequest} from '@model/common/page-request';
import * as fromSupervisionWorkQueue from '@feature/supervision-workqueue/reducers';
import {
  ChangeOwnerSuccess,
  RemoveOwnerSuccess,
  SupervisionTaskActionType
} from '@feature/application/supervision/actions/supervision-task-actions';
import {SupervisionTaskSearchCriteria} from '@model/application/supervision/supervision-task-search-criteria';
import * as fromAuth from '@feature/auth/reducers';
import {ObjectUtil} from '@util/object.util';
import {WorkQueueTab} from '@feature/workqueue/workqueue-tab';

@Injectable()
export class SupervisionTaskSearchEffects {
  constructor(private actions: Actions,
              private store: Store<fromRoot.State>,
              private taskService: SupervisionTaskService) {}

  
  search: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Search>(SupervisionTaskSearchActionType.Search),
    switchMap(action => this.setTargetTypeSpecificParameters(action)),
    switchMap(action =>
      this.taskService.search(action.payload.query, action.payload.sort, action.payload.pageRequest).pipe(
        map(result => new SearchSuccess(action.targetType, result)),
        catchError(error => of(new SearchFailed(action.targetType, error)))
      ))
  ));

  
  onOwnerChanges: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ChangeOwnerSuccess | RemoveOwnerSuccess>(
      SupervisionTaskActionType.ChangeOwnerSuccess,
      SupervisionTaskActionType.RemoveOwnerSuccess
    ),
    switchMap(() => this.getCurrentWorkQueueSearch()),
    switchMap(([search, sort, pageRequest]) => [
      new Search(ActionTargetType.SupervisionTaskWorkQueue, search, sort, pageRequest),
      new ClearSelected(ActionTargetType.SupervisionTaskWorkQueue)
    ])
  ));

  private getCurrentWorkQueueSearch(): Observable<[SupervisionTaskSearchCriteria, Sort, PageRequest]> {
    return combineLatest(
      this.store.pipe(select(fromSupervisionWorkQueue.getParameters), filter(search => !!search)),
      this.store.pipe(select(fromSupervisionWorkQueue.getSort)),
      this.store.pipe(select(fromSupervisionWorkQueue.getPageRequest)),
    );
  }

  /**
   * In case of Workqueue current tab must be checked. When current tab is user's own tab
   * query should be updated to include only current users supervision tasks.
   */
  private setTargetTypeSpecificParameters(action: Search): Observable<Search> {
    if (action.targetType === ActionTargetType.SupervisionTaskWorkQueue) {
      return combineLatest(
        this.store.pipe(select(fromSupervisionWorkQueue.getTab)),
        this.store.pipe(select(fromAuth.getUser), filter(user => !!user))
      ).pipe(
        map(([tab, user]) => {
          const payload = action.payload;
          const queryCopy = ObjectUtil.clone(payload.query);
          queryCopy.owners = WorkQueueTab.OWN === tab ? [user.id] : payload.query.owners;
          return new Search(action.targetType, queryCopy, payload.sort, payload.pageRequest);
        })
      );
    } else {
      return of(action);
    }
  }
}
