import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {SupervisionTaskForm} from './supervision-task-form';
import {ApplicationStore} from '../../../service/application/application-store';
import {NotificationService} from '../../notification/notification.service';
import {User} from '../../../model/user/user';
import {CurrentUser} from '../../../service/user/current-user';
import {SupervisionTaskStore} from '../../../service/supervision/supervision-task-store';
import {EnumUtil} from '../../../util/enum.util';
import {SupervisionTaskType, isAutomaticSupervisionTaskType} from '../../../model/application/supervision/supervision-task-type';
import {SupervisionTaskStatusType} from '../../../model/application/supervision/supervision-task-status-type';
import {UserSearchCriteria} from '../../../model/user/user-search-criteria';
import {RoleType} from '../../../model/user/role-type';
import {ArrayUtil} from '../../../util/array-util';
import {UserHub} from '../../../service/user/user-hub';
import {
  SUPERVISION_APPROVAL_MODAL_CONFIG,
  SupervisionApprovalModalComponent,
  SupervisionApprovalModalType,
  SupervisionApprovalResult
} from './supervision-approval-modal.component';
import {MatDialog, MatDialogRef} from '@angular/material';
import {SupervisionTask} from '../../../model/application/supervision/supervision-task';
import {filter, map, switchMap} from 'rxjs/internal/operators';

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
              private store: SupervisionTaskStore,
              private currentUser: CurrentUser,
              private userHub: UserHub,
              private dialog: MatDialog,
              private notification: NotificationService) {
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
      this.store.removeTask(this.applicationStore.snapshot.application.id, task.id)
        .subscribe(
          status => {
            this.onRemove.emit();
            this.notification.translateSuccess('supervision.task.action.remove');
          },
          error => this.notification.translateError(error));
    } else {
      this.onRemove.emit();
    }
  }

  save(): void {
    const formValue = <SupervisionTaskForm>this.form.value;
    this.form.disable();
    this.store.saveTask(this.applicationStore.snapshot.application.id, SupervisionTaskForm.to(formValue))
      .subscribe(
        c => this.notification.translateSuccess('supervision.task.action.save'),
        error => {
          this.form.enable();
          this.notification.translateError(error);
        });
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
    this.openModal('APPROVE').afterClosed().pipe(
      filter(result => !!result),
      map(result => this.taskWithResult(SupervisionTaskStatusType.APPROVED, result)),
      switchMap(task => this.store.approve(task))
    ).subscribe(
      saved => this.notification.translateSuccess('supervision.task.action.approve'),
      err => this.notification.translateSuccess('supervision.task.error.approve'));
  }

  reject(): void {
    this.openModal('REJECT').afterClosed().pipe(
      filter(result => !!result),
      switchMap(result => this.store.reject(
        this.taskWithResult(SupervisionTaskStatusType.REJECTED, result),
        result.newSupervisionDate))
    ).subscribe(
      saved => this.notification.translateSuccess('supervision.task.action.reject'),
      err => this.notification.translateSuccess('supervision.task.error.reject'));
  }

  private taskWithResult(status: SupervisionTaskStatusType, result: SupervisionApprovalResult): SupervisionTask {
    const formValue = <SupervisionTaskForm>this.form.value;
    const task = SupervisionTaskForm.to(formValue);
    task.status = status;
    task.actualFinishingTime = new Date();
    task.result = result.result;
    return task;
  }

  private openModal(type: SupervisionApprovalModalType): MatDialogRef<SupervisionApprovalModalComponent> {
    const config = {
      ...SUPERVISION_APPROVAL_MODAL_CONFIG,
      data: {
        type: type
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
}
