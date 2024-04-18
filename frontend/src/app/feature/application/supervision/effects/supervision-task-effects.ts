import {Injectable} from '@angular/core';
import {Action, Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import * as fromSupervision from '@feature/application/supervision/reducers';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {SupervisionTaskService} from '@service/supervision/supervision-task.service';
import {EMPTY, from, Observable, of} from 'rxjs/index';
import {
  Approve,
  ApproveSuccess, ChangeOwner, ChangeOwnerSuccess,
  Load,
  LoadFailed,
  LoadSuccess,
  Reject,
  RejectSuccess,
  Remove, RemoveOwner, RemoveOwnerSuccess,
  RemoveSuccess,
  Save,
  SaveSuccess,
  SupervisionTaskActionType
} from '@feature/application/supervision/actions/supervision-task-actions';
import * as TagActions from '@feature/application/actions/application-tag-actions';
import {withLatestExisting} from '@feature/common/with-latest-existing';
import {catchError, map, switchMap, take, tap} from 'rxjs/internal/operators';
import {NotifyFailure, NotifySuccess} from '@feature/notification/actions/notification-actions';
import {Load as LoadInvoices} from '@feature/application/invoicing/actions/invoice-actions';
import {Load as LoadChargeBasis} from '@feature/application/invoicing/actions/charge-basis-actions';
import {Load as LoadComments} from '@feature/comment/actions/comment-actions';
import {ActionTargetType} from '@feature/allu/actions/action-target-type';
import {DateReportingService} from '@service/application/date-reporting.service';
import {SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {ReportOperationalCondition, ReportWorkFinished} from '@feature/application/actions/date-reporting-actions';
import {Application} from '@model/application/application';
import {ApplicationStore} from '@service/application/application-store';
import {ApplicationStatus} from '@model/application/application-status';
import {StatusChangeInfo} from '@model/application/status-change-info';

const requiresTagReload = [
  SupervisionTaskActionType.SaveSuccess,
  SupervisionTaskActionType.RemoveSuccess,
  SupervisionTaskActionType.ApproveSuccess,
  SupervisionTaskActionType.RejectSuccess
];

@Injectable()
export class SupervisionTaskEffects {
  constructor(private actions: Actions,
              private store: Store<fromRoot.State>,
              private taskService: SupervisionTaskService,
              private dateReporting: DateReportingService,
              private applicationStore: ApplicationStore) {}

  
  load: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Load>(SupervisionTaskActionType.Load),
    withLatestExisting(this.store.select(fromApplication.getCurrentApplication)),
    switchMap(([action, app]) => this.taskService.findTasksByApplicationId(app.id).pipe(
      map(tasks => new LoadSuccess(tasks)),
      catchError(error => from([
        new LoadFailed(),
        new NotifyFailure(error)
      ]))
    ))
  ));

  
  save: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Save>(SupervisionTaskActionType.Save),
    switchMap(action => this.taskService.save(action.payload).pipe(
      switchMap(task => [
        new SaveSuccess(task),
        new NotifySuccess('supervision.task.action.save')
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  remove: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Remove>(SupervisionTaskActionType.Remove),
    switchMap(action => this.taskService.remove(action.payload).pipe(
      switchMap(() => [
        new RemoveSuccess(action.payload),
        new NotifySuccess('supervision.task.action.remove')
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  approve: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Approve>(SupervisionTaskActionType.Approve),
    switchMap(action => this.taskService.approve(action.payload.task).pipe(
      switchMap((task) => this.reportDatesOnApproval(task.applicationId, action.payload.reportedDate, task.type).pipe(
        switchMap(app => this.handleStatusChange(app, action.payload.status, action.payload.changeInfo)),
        switchMap(app => [
          new ApproveSuccess(task),
          this.applicationStore.setAndAction(app),
          new NotifySuccess('supervision.task.action.approve')
        ])
      )),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  reject: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Reject>(SupervisionTaskActionType.Reject),
    switchMap(action => this.taskService.reject(action.payload.task, action.payload.newSupervisionDate).pipe(
      switchMap((task) => [
        new RejectSuccess(task),
        new NotifySuccess('supervision.task.action.reject')
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  changeOwner: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ChangeOwner>(SupervisionTaskActionType.ChangeOwner),
    switchMap(action => this.taskService.changeOwner(action.payload.ownerId, action.payload.taskIds).pipe(
      switchMap(() => [
        new Load(),
        new ChangeOwnerSuccess(),
        new NotifySuccess('supervision.task.action.handlerChanged')
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  removeOwner: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<RemoveOwner>(SupervisionTaskActionType.RemoveOwner),
    switchMap(action => this.taskService.removeOwner(action.payload).pipe(
      switchMap(() => [
        new Load(),
        new RemoveOwnerSuccess(),
        new NotifySuccess('supervision.task.action.handlerRemoved')
      ]),
      catchError(error => of(new NotifyFailure(error)))
    ))
  ));

  
  reloadTags: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Action>(...requiresTagReload),
    map(() => new TagActions.Load())
  ));

  
  reloadInvoicing: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Action>(SupervisionTaskActionType.ApproveSuccess),
    switchMap(() => [new LoadChargeBasis(), new LoadInvoices()])
  ));

  
  reloadComments: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<Action>(SupervisionTaskActionType.ApproveSuccess),
    map(() => new LoadComments(ActionTargetType.Application))
  ));

  
  removeDanglingOperationalConditionTask: Observable<Action> = createEffect(() => this.actions.pipe(
    ofType<ApproveSuccess>(SupervisionTaskActionType.ApproveSuccess),
    withLatestExisting(this.store.select(fromSupervision.getOpenOperationalConditionTask)),
    map(([action, task]) => new RemoveSuccess(task.id))
  ));

  private reportDatesOnApproval(appId: number, date: Date, type: SupervisionTaskType): Observable<Application> {
    if (date !== undefined) {
      if (type === SupervisionTaskType.OPERATIONAL_CONDITION) {
        return this.dateReporting.reportOperationalCondition(appId, date);
      } else if (type === SupervisionTaskType.FINAL_SUPERVISION) {
        return this.dateReporting.reportWorkFinished(appId, date);
      }
    }
    return this.store.select(fromApplication.getCurrentApplication).pipe(take(1));
  }

  private handleStatusChange(app: Application, target: ApplicationStatus, changeInfo?: StatusChangeInfo): Observable<Application> {
    if (target) {
      return this.applicationStore.changeStatus(app.id, target, changeInfo);
    } else {
      return of(app);
    }
  }
}
