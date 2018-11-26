import {Action, select, Store} from '@ngrx/store';
import {Actions, Effect, ofType} from '@ngrx/effects';
import * as fromApplication from '../reducers';
import {MetadataService} from '../../../service/meta/metadata.service';
import {from, Observable, of} from 'rxjs/index';
import {catchError, filter, map, switchMap, tap, withLatestFrom} from 'rxjs/internal/operators';
import {NumberUtil} from '../../../util/number.util';
import {ApplicationMetaActionType} from '../actions/application-meta-actions';
import * as ApplicationAction from '../actions/application-actions';
import * as MetaAction from '../actions/application-meta-actions';
import {Injectable} from '@angular/core';
import {ApplicationActionType} from '@feature/application/actions/application-actions';
import {ApplicationService} from '@service/application/application.service';
import {ApplicationStore} from '@service/application/application-store';
import {NotifyFailure} from '@feature/notification/actions/notification-actions';
import {withLatestExisting} from '@feature/common/with-latest-existing';

@Injectable()
export class ApplicationEffects {
  constructor(private actions: Actions,
              private store: Store<fromApplication.State>,
              private applicationStore: ApplicationStore,
              private applicationService: ApplicationService,
              private metadataService: MetadataService) {}

  @Effect()
  load: Observable<Action> = this.actions.pipe(
    ofType<ApplicationAction.Load>(ApplicationActionType.Load),
    filter(action => NumberUtil.isDefined(action.payload)),
    switchMap(action => this.applicationService.get(action.payload).pipe(
      tap(application => this.applicationStore.setApplication(application)), // TODO: Remove when all api calls use ngrx
      map(application => new ApplicationAction.LoadSuccess(application)),
      catchError(error => from([
        new ApplicationAction.LoadFailed(error),
        new NotifyFailure(error)
      ]))
    ))
  );

  @Effect()
  loadMeta: Observable<Action> = this.actions.pipe(
    ofType<MetaAction.Load>(ApplicationMetaActionType.Load),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, application]) => this.metadataService.loadByApplicationType(application.type).pipe(
      map(meta => new MetaAction.LoadSuccess(meta)),
      catchError(error => of(new MetaAction.LoadFailed(error)))
    ))
  );

  @Effect()
  removeClientApplicationData: Observable<Action> = this.actions.pipe(
    ofType<ApplicationAction.RemoveClientApplicationData>(ApplicationActionType.RemoveClientApplicationData),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, application]) => this.applicationService.removeClientApplicationData(application.id).pipe(
      tap(app => this.applicationStore.setApplication(app)), // TODO: Remove when all api calls use ngrx
      map(app => new ApplicationAction.LoadSuccess(app)),
      catchError(error => from([
        new ApplicationAction.LoadFailed(error),
        new NotifyFailure(error)
      ]))
    ))
  );
}
