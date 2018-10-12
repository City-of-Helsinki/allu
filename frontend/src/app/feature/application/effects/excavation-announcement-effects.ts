import {Injectable} from '@angular/core';
import {Actions, Effect, ofType} from '@ngrx/effects';
import * as fromApplication from '@feature/application/reducers';
import {Action, select, Store} from '@ngrx/store';
import {ApplicationStore} from '@service/application/application-store';
import {ExcavationAnnouncementService} from '@service/application/excavation-announcement.service';
import {Observable, of} from 'rxjs/index';
import {
  ExcavationAnnouncementActionType,
  ReportCustomerOperationalCondition,
  ReportCustomerWorkFinished,
  ReportOperationalCondition,
  ReportWorkFinished,
  SetRequiredTasks
} from '@feature/application/actions/excavation-announcement-actions';
import * as SupervisionTaskActions from '@feature/application/supervision/actions/supervision-task-actions';
import {catchError, map, switchMap} from 'rxjs/internal/operators';
import {withLatestExisting} from '@feature/common/with-latest-existing';
import {NotifyFailure, NotifySuccess} from '@feature/notification/actions/notification-actions';
import {findTranslation} from '@util/translations';

@Injectable()
export class ExcavationAnnouncementEffects {
  constructor(private actions: Actions,
              private store: Store<fromApplication.State>,
              private applicationStore: ApplicationStore,
              private excavationAnnouncementService: ExcavationAnnouncementService) {}

  @Effect()
  reportOperationalCondition: Observable<Action> = this.actions.pipe(
    ofType<ReportOperationalCondition>(ExcavationAnnouncementActionType.ReportOperationalCondition),
    withLatestExisting(this.store.select(fromApplication.getCurrentApplication)),
    switchMap(([action, app]) => this.excavationAnnouncementService.reportOperationalCondition(app.id, action.payload).pipe(
      switchMap(updated => [
        this.applicationStore.setAndAction(updated),
        new NotifySuccess(findTranslation('application.excavationAnnouncement.action.reportOperationalCondition'))
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  reportWorkFinished: Observable<Action> = this.actions.pipe(
    ofType<ReportWorkFinished>(ExcavationAnnouncementActionType.ReportWorkFinished),
    withLatestExisting(this.store.select(fromApplication.getCurrentApplication)),
    switchMap(([action, app]) => this.excavationAnnouncementService.reportWorkFinished(app.id, action.payload).pipe(
      switchMap(updated => [
        this.applicationStore.setAndAction(updated),
        new NotifySuccess(findTranslation('application.excavationAnnouncement.action.reportWorkFinished'))
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  reportCustomerOperationalCondition: Observable<Action> = this.actions.pipe(
    ofType<ReportCustomerOperationalCondition>(ExcavationAnnouncementActionType.ReportCustomerOperationalCondition),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) => this.excavationAnnouncementService.reportCustomerOperationalCondition(app.id, action.payload).pipe(
      switchMap(updated => [
        this.applicationStore.setAndAction(updated),
        new NotifySuccess(findTranslation('application.excavationAnnouncement.action.reportCustomerOperationalCondition')),
        new SupervisionTaskActions.Load()
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  reportCustomerWorkFinished: Observable<Action> = this.actions.pipe(
    ofType<ReportCustomerWorkFinished>(ExcavationAnnouncementActionType.ReportCustomerWorkFinished),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) => this.excavationAnnouncementService.reportCustomerWorkFinished(app.id, action.payload).pipe(
      switchMap(updated => [
        this.applicationStore.setAndAction(updated),
        new NotifySuccess(findTranslation('application.excavationAnnouncement.action.reportCustomerWorkFinished')),
        new SupervisionTaskActions.Load()
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );

  @Effect()
  setRequiredTasks: Observable<Action> = this.actions.pipe(
    ofType<SetRequiredTasks>(ExcavationAnnouncementActionType.SetRequiredTasks),
    withLatestExisting(this.store.select(fromApplication.getCurrentApplication)),
    switchMap(([action, app]) => this.excavationAnnouncementService.setRequiredTasks(app.id, action.payload).pipe(
      map(updated => this.applicationStore.setAndAction(updated)),
      catchError(error => of(new NotifyFailure(error)))
    ))
  );
}
