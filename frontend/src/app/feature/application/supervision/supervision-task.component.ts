import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {SupervisionTaskForm} from './supervision-task-form';
import {ApplicationStore} from '@service/application/application-store';
import {User} from '@model/user/user';
import {CurrentUser} from '@service/user/current-user';
import {EnumUtil} from '@util/enum.util';
import {isAutomaticSupervisionTaskType, SupervisionTaskType} from '@model/application/supervision/supervision-task-type';
import {SupervisionTaskStatusType} from '@model/application/supervision/supervision-task-status-type';
import {UserSearchCriteria} from '@model/user/user-search-criteria';
import {RoleType} from '@model/user/role-type';
import {ArrayUtil} from '@util/array-util';
import {UserHub} from '@service/user/user-hub';
import {
  SUPERVISION_APPROVAL_MODAL_CONFIG,
  SupervisionApprovalModalComponent,
  SupervisionApprovalResolutionType,
  SupervisionApprovalResult
} from './supervision-approval-modal.component';
import {MatDialog, MatDialogRef} from '@angular/material';
import {SupervisionTask} from '@model/application/supervision/supervision-task';
import {filter, map, take} from 'rxjs/internal/operators';
import {Store} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Approve, Reject, Remove, Save} from '@feature/application/supervision/actions/supervision-task-actions';
import {Application} from '@model/application/application';
import {ApplicationType} from '@model/application/type/application-type';
import {ExcavationAnnouncement} from '@model/application/excavation-announcement/excavation-announcement';
import {Some} from '@util/option';
import {ReportOperationalCondition, ReportWorkFinished} from '@feature/application/actions/excavation-announcement-actions';

@Component({
  selector: 'supervision-task',
  templateUrl: './supervision-task.component.html',
  styleUrls: [
    './supervision-task.component.scss'
  ]
})
export class SupervisionTaskComponent implements OnInit, OnDestroy {
  @Input() form: FormGroup;
  @Input() supervisors: Array<User> = [];
  @Output() onRemove = new EventEmitter<void>();

  taskTypes: string[] = [];
  statusTypes = EnumUtil.enumValues(SupervisionTaskStatusType);
  canEdit = false;
  canApprove = false;
  canRemove = false;

  private originalEntry: SupervisionTaskForm;

  constructor(private applicationStore: ApplicationStore,
              private store: Store<fromRoot.State>,
              private currentUser: CurrentUser,
              private userHub: UserHub,
              private dialog: MatDialog) {
  }

  ngOnInit(): void {
    const formValue = this.form.value;
    if (formValue.id) {
      this.form.disable();
    } else {
      this.preferredSupervisor();
    }
    if (formValue.automatic) {
      this.taskTypes = EnumUtil.enumValues(SupervisionTaskType);
    } else {
      this.taskTypes = EnumUtil.enumValues(SupervisionTaskType)
        .filter(type => !isAutomaticSupervisionTaskType(SupervisionTaskType[type]));
    }
    this.currentUserCanEdit(formValue.creatorId);
    this.currentUserCanApprove(formValue.ownerId, formValue.status);
    this.userCanRemove(formValue.status);
  }

  ngOnDestroy(): void {
  }

  remove(): void {
    const task = this.form.value;
    if (task.id) {
      this.store.dispatch(new Remove(task.id));
    }
    this.onRemove.emit();
  }

  save(): void {
    const formValue = <SupervisionTaskForm>this.form.getRawValue();
    this.store.select(fromApplication.getCurrentApplication).pipe(
      map(app => {
        const task = SupervisionTaskForm.to(formValue);
        task.applicationId = task.applicationId || app.id;
        return task;
      }),
      take(1)
    ).subscribe(task => this.store.dispatch(new Save(task)));
  }

  cancel(): void {
    // Reset form to original value when such exists,
    // otherwise treat as remove
    if (this.originalEntry) {
      this.form.patchValue(this.originalEntry);
      this.originalEntry = undefined;
      this.form.disable();
    } else {
      this.onRemove.emit();
    }
  }

  edit(): void {
    this.form.enable();
    this.originalEntry = this.form.value;
    if (this.form.value.automatic) {
      this.form.controls['type'].disable();
    }
  }

  approve(): void {
    this.openModal(SupervisionApprovalResolutionType.APPROVE).afterClosed().pipe(
      filter(result => !!result),
    ).subscribe((result: SupervisionApprovalResult) => this.handleApproval(result));
  }

  reject(): void {
    this.openModal(SupervisionApprovalResolutionType.REJECT).afterClosed().pipe(
      filter(result => !!result)
    ).subscribe(result => this.store.dispatch(
      new Reject(this.taskWithResult(SupervisionTaskStatusType.REJECTED, result), result.newSupervisionDate)
    ));
  }

  private handleApproval(result: SupervisionApprovalResult): void {
    const task = this.taskWithResult(SupervisionTaskStatusType.APPROVED, result);
    this.store.dispatch(new Approve(task));
    Some(result.reportedDate).do(date => this.reportDatesOnApproval(date, task.type));
  }

  private reportDatesOnApproval(date: Date, type: SupervisionTaskType): void {
    if (type === SupervisionTaskType.OPERATIONAL_CONDITION) {
      this.store.dispatch(new ReportOperationalCondition(date));
    } else if (type === SupervisionTaskType.FINAL_SUPERVISION) {
      this.store.dispatch(new ReportWorkFinished(date));
    }
  }

  private taskWithResult(status: SupervisionTaskStatusType, result: SupervisionApprovalResult): SupervisionTask {
    const formValue = <SupervisionTaskForm>this.form.value;
    const task = SupervisionTaskForm.to(formValue);
    task.status = status;
    task.actualFinishingTime = new Date();
    task.result = result.result;
    return task;
  }

  private openModal(type: SupervisionApprovalResolutionType): MatDialogRef<SupervisionApprovalModalComponent> {
    const application = this.applicationStore.snapshot.application;
    const task = SupervisionTaskForm.to(this.form.value);
    const reportedDate = SupervisionApprovalResolutionType.APPROVE === type
      ? this.proposedReportedDate(task, application)
      : undefined;

    const config = {
      ...SUPERVISION_APPROVAL_MODAL_CONFIG,
      data: {
        resolutionType: type,
        taskType: task.type,
        applicationType: application.type,
        reportedDate
      }
    };

    return this.dialog.open(SupervisionApprovalModalComponent, config);
  }

  private currentUserCanEdit(creatorId: number): void {
    if (creatorId === undefined) {
      this.canEdit = true;
    } else {
      this.currentUser.isCurrentUser(creatorId).subscribe(isCurrent => this.canEdit = isCurrent);
    }
  }

  private currentUserCanApprove(ownerId: number, statusName: string): void {
    const status = SupervisionTaskStatusType[statusName];
    this.currentUser.isCurrentUser(ownerId).subscribe(isCurrent => {
      this.canApprove = isCurrent && SupervisionTaskStatusType.OPEN === status;
    });
  }

  private userCanRemove(statusName: string): void {
    this.canRemove = statusName === undefined || SupervisionTaskStatusType.OPEN === SupervisionTaskStatusType[statusName];
  }

  private preferredSupervisor(): void {
    const app = this.applicationStore.snapshot.application;
    const criteria = new UserSearchCriteria(RoleType.ROLE_SUPERVISE, app.type, app.firstLocation.effectiveCityDistrictId);
    this.userHub.searchUsers(criteria).pipe(
      map(preferred => ArrayUtil.first(preferred)),
      filter(preferred => !!preferred)
    ).subscribe(preferred => this.form.patchValue({ownerId: preferred.id}));
  }

  private proposedReportedDate(task: SupervisionTask, application: Application): Date {
    if (ApplicationType.EXCAVATION_ANNOUNCEMENT === application.type) {
      const extension = <ExcavationAnnouncement> application.extension;
      if (SupervisionTaskType.OPERATIONAL_CONDITION === task.type) {
        return extension.customerWinterTimeOperation || extension.winterTimeOperation;
      } else if (SupervisionTaskType.FINAL_SUPERVISION === task.type) {
        return extension.customerWorkFinished || application.endTime;
      }
    }
    return undefined;
  }
}
