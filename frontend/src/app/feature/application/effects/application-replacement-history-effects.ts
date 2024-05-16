import {Action, select, Store} from '@ngrx/store';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import * as fromApplication from '../reducers';
import {Observable, of} from 'rxjs/index';
import {catchError, map, switchMap} from 'rxjs/internal/operators';
import * as ApplicationAction from '../actions/application-actions';
import * as ApplicationReplacementHistoryActions from '../actions/application-replacement-history-actions';
import {Injectable} from '@angular/core';
import {ApplicationActionType} from '@feature/application/actions/application-actions';
import {ApplicationService} from '@service/application/application.service';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {withLatestExisting} from '@feature/common/with-latest-existing';
import {
  ApplicationReplacementHistoryActionType
} from '@feature/application/actions/application-replacement-history-actions';

@Injectable()
export class ApplicationReplacementHistoryEffects {
  constructor(private actions: Actions,
              private store: Store<fromApplication.State>,
              private applicationService: ApplicationService) {}

  
  load: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ApplicationReplacementHistoryActions.Load>(ApplicationReplacementHistoryActionType.Load),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) => this.applicationService.getReplacementHistory(app.id).pipe(
      map(history => new ApplicationReplacementHistoryActions.LoadSuccess(history)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  onApplicationLoad: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ApplicationAction.LoadSuccess>(ApplicationActionType.LoadSuccess),
    map(() => new ApplicationReplacementHistoryActions.Load())
  ));
}
