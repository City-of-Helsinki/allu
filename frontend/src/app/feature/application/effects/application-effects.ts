import {Action, select, Store} from '@ngrx/store';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import * as fromApplication from '../reducers';
import {MetadataService} from '../../../service/meta/metadata.service';
import {from, Observable, of} from 'rxjs/index';
import {catchError, filter, map, switchMap, tap} from 'rxjs/internal/operators';
import {NumberUtil} from '../../../util/number.util';
import * as MetaAction from '../actions/application-meta-actions';
import {ApplicationMetaActionType} from '../actions/application-meta-actions';
import * as ApplicationAction from '../actions/application-actions';
import {Injectable} from '@angular/core';
import {ApplicationActionType, LoadDistributionSuccess, SaveDistributionSuccess} from '@feature/application/actions/application-actions';
import {ApplicationService} from '@service/application/application.service';
import {ApplicationStore} from '@service/application/application-store';
import {NotifyFailure, NotifySuccess} from '@feature/notification/actions/notification-actions';
import {withLatestExisting} from '@feature/common/with-latest-existing';
import {ClearCoordinates} from '@feature/map/actions/address-search-actions';
import {findTranslation} from '@util/translations';
import {DistributionEntry} from '@model/common/distribution-entry';

@Injectable()
export class ApplicationEffects {
  constructor(private actions: Actions,
              private store: Store<fromApplication.State>,
              private applicationStore: ApplicationStore,
              private applicationService: ApplicationService,
              private metadataService: MetadataService) {}

  
  load: Observable<Action> = createEffect(() => this.actions.pipe(
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
  ));

  
  loadMeta: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<MetaAction.Load>(ApplicationMetaActionType.Load),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, application]) => this.metadataService.loadByApplicationType(application.type).pipe(
      map(meta => new MetaAction.LoadSuccess(meta)),
      catchError(error => of(new MetaAction.LoadFailed(error)))
    ))
  ));

  
  removeClientApplicationData: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ApplicationAction.RemoveClientApplicationData>(ApplicationActionType.RemoveClientApplicationData),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    filter(([action, application]) => !!application.clientApplicationData),
    switchMap(([action, application]) => this.applicationService.removeClientApplicationData(application.id).pipe(
      tap(app => this.applicationStore.setApplication(app)), // TODO: Remove when all api calls use ngrx
      map(app => new ApplicationAction.LoadSuccess(app)),
      catchError(error => from([
        new ApplicationAction.LoadFailed(error),
        new NotifyFailure(error)
      ]))
    ))
  ));

  
  changeOwner: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ApplicationAction.ChangeOwner>(ApplicationActionType.ChangeOwner),
    switchMap(action => this.applicationService.changeOwner(action.payload.ownerId, action.payload.applicationIds).pipe(
      switchMap(() => [
        new ApplicationAction.ChangeOwnerSuccess(),
        new NotifySuccess(findTranslation('workqueue.notifications.ownerChanged')),
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  removeOwner: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ApplicationAction.RemoveOwner>(ApplicationActionType.RemoveOwner),
    switchMap(action => this.applicationService.removeOwner(action.payload).pipe(
      switchMap(() => [
        new ApplicationAction.RemoveOwnerSuccess(),
        new NotifySuccess(findTranslation('workqueue.notifications.ownerRemoved'))
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  removeOwnerNotification: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ApplicationAction.RemoveOwnerNotification>(ApplicationActionType.RemoveOwnerNotification),
    switchMap(action => this.applicationService.removeOwnerNotification(action.payload).pipe(
      switchMap(() => [
        new ApplicationAction.RemoveOwnerNotificationSuccess(),
        new ApplicationAction.Load(action.payload),
        new NotifySuccess(findTranslation('application.action.removeOwnerNotification'))
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  saveDistribution: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ApplicationAction.SaveDistributionAndNotify | ApplicationAction.SaveDistribution>(
      ApplicationActionType.SaveDistributionAndNotify, ApplicationActionType.SaveDistribution
    ),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) => this.applicationService.updateDistribution(app.id, action.payload).pipe(
      switchMap(distribution => this.onSaveDistributionSuccess(action, distribution)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  loadDistribution: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ApplicationAction.LoadDistribution>(ApplicationActionType.LoadDistribution),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) => this.applicationService.getDistribution(app.id).pipe(
      map(distribution => new LoadDistributionSuccess(distribution)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  onApplicationLoad: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ApplicationAction.Load>(ApplicationActionType.Load),
    map(() => new ClearCoordinates())
  ));

  private onSaveDistributionSuccess(action: Action, result: DistributionEntry[]): Action[] {
    return action.type === ApplicationActionType.SaveDistribution
      ? [new SaveDistributionSuccess(result)]
      : [new SaveDistributionSuccess(result), new NotifySuccess('decision.distribution.action.save')];
  }
}
