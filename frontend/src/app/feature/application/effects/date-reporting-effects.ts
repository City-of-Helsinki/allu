import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import * as fromApplication from '@feature/application/reducers';
import {Action, select, Store} from '@ngrx/store';
import {ApplicationStore} from '@service/application/application-store';
import {Observable, of} from 'rxjs/index';
import {
  ReportCustomerOperationalCondition,
  ReportCustomerValidity, ReportLocationCustomerValidity,
  ReportOperationalCondition,
} from '@feature/application/actions/date-reporting-actions';
import * as SupervisionTaskActions from '@feature/application/supervision/actions/supervision-task-actions';
import {catchError, map, switchMap} from 'rxjs/internal/operators';
import {withLatestExisting} from '@feature/common/with-latest-existing';
import {NotifyFailure, NotifySuccess} from '@feature/notification/actions/notification-actions';
import {findTranslation} from '@util/translations';
import * as TagAction from '@feature/application/actions/application-tag-actions';
import {DateReportingActionType, ReportCustomerWorkFinished, ReportWorkFinished} from '@feature/application/actions/date-reporting-actions';
import {DateReportingService} from '@service/application/date-reporting.service';

@Injectable()
export class DateReportingEffects {
  constructor(private actions: Actions,
              private store: Store<fromApplication.State>,
              private applicationStore: ApplicationStore,
              private dateReporting: DateReportingService) {}

  
  reportOperationalCondition: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ReportOperationalCondition>(DateReportingActionType.ReportOperationalCondition),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) => this.dateReporting.reportOperationalCondition(app.id, action.payload).pipe(
      switchMap(updated => [
        this.applicationStore.setAndAction(updated),
        new NotifySuccess(findTranslation('application.action.reportOperationalCondition'))
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  reportWorkFinished: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ReportWorkFinished>(DateReportingActionType.ReportWorkFinished),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) => this.dateReporting.reportWorkFinished(app.id, action.payload).pipe(
      switchMap(updated => [
        this.applicationStore.setAndAction(updated),
        new NotifySuccess(findTranslation('application.action.reportWorkFinished'))
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  reportCustomerOperationalCondition: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ReportCustomerOperationalCondition>(DateReportingActionType.ReportCustomerOperationalCondition),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) => this.dateReporting.reportCustomerOperationalCondition(app.id, action.payload).pipe(
      switchMap(updated => [
        this.applicationStore.setAndAction(updated),
        new NotifySuccess(findTranslation('application.action.reportCustomerOperationalCondition')),
        new SupervisionTaskActions.Load(),
        new TagAction.Load()
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  reportCustomerWorkFinished: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ReportCustomerWorkFinished>(DateReportingActionType.ReportCustomerWorkFinished),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) => this.dateReporting.reportCustomerWorkFinished(app.id, action.payload).pipe(
      switchMap(updated => [
        this.applicationStore.setAndAction(updated),
        new NotifySuccess(findTranslation('application.action.reportCustomerWorkFinished')),
        new SupervisionTaskActions.Load()
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  reportCustomerValidity: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ReportCustomerValidity>(DateReportingActionType.ReportCustomerValidity),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) => this.dateReporting.reportCustomerValidity(app.id, action.payload).pipe(
      switchMap(updated => [
        this.applicationStore.setAndAction(updated),
        new NotifySuccess(findTranslation('application.action.reportCustomerValidity')),
        new TagAction.Load()
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  reportLocationCustomerValidity: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ReportLocationCustomerValidity>(DateReportingActionType.ReportLocationCustomerValidity),
    withLatestExisting(this.store.pipe(select(fromApplication.getCurrentApplication))),
    switchMap(([action, app]) =>
      this.dateReporting.reportLocationCustomerValidity(app.id, action.payload.id, action.payload.report).pipe(
        switchMap(updated => [
          this.applicationStore.setAndAction(updated),
          new NotifySuccess(findTranslation('application.action.reportCustomerValidity')),
          new SupervisionTaskActions.Load(),
          new TagAction.Load()
        ]),
        catchError(error => of(new NotifyFailure(error)))
      ))
  ));
}
