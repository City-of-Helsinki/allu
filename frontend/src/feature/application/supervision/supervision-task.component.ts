import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {SupervisionTaskForm} from './supervision-task-form';
import {ApplicationState} from '../../../service/application/application-state';
import {NotificationService} from '../../../service/notification/notification.service';
import {User} from '../../../model/user/user';
import {Some} from '../../../util/option';
import {CurrentUser} from '../../../service/user/current-user';
import {SupervisionTaskStore} from '../../../service/supervision/supervision-task-store';
import {EnumUtil} from '../../../util/enum.util';
import {SupervisionTaskType} from '../../../model/application/supervision/supervision-task-type';
import {SupervisionTaskStatusType} from '../../../model/application/supervision/supervision-task-status-type';
import {NumberUtil} from '../../../util/number.util';
import {UserSearchCriteria} from '../../../model/user/user-search-criteria';
import {RoleType} from '../../../model/user/role-type';
import {ArrayUtil} from '../../../util/array-util';
import {UserHub} from '../../../service/user/user-hub';

@Component({
  selector: 'supervision-task',
  template: require('./supervision-task.component.html'),
  styles: [
    require('./supervision-task.component.scss')
  ]
})
export class SupervisionTaskComponent implements OnInit, OnDestroy {
  @Input() form: FormGroup;
  @Input() supervisors: Array<User> = [];
  @Output() onRemove = new EventEmitter<void>();

  taskTypes = EnumUtil.enumValues(SupervisionTaskType);
  statusTypes = EnumUtil.enumValues(SupervisionTaskStatusType);
  canEdit = false;

  private originalEntry: SupervisionTaskForm;

  constructor(private applicationState: ApplicationState,
              private supervisionTaskStore: SupervisionTaskStore,
              private currentUser: CurrentUser,
              private userHub: UserHub) {
  }

  ngOnInit(): void {
    const formValue = this.form.value;
    if (formValue.id) {
      this.form.disable();
    } else {
      this.preferredSupervisor();
    }
    this.currentUserCanEdit(formValue.creatorId);
  }

  ngOnDestroy(): void {
  }

  remove(): void {
    const task = this.form.value;
    if (task.id) {
      this.supervisionTaskStore.removeTask(this.applicationState.application.id, task.id)
        .subscribe(
          status => {
            this.onRemove.emit();
            NotificationService.translateMessage('supervision.task.action.remove');
          },
          error => NotificationService.translateError(error));
    } else {
      this.onRemove.emit();
    }
  }

  save(): void {
    const formValue = <SupervisionTaskForm>this.form.value;
    this.form.disable();
    this.supervisionTaskStore.saveTask(this.applicationState.application.id, SupervisionTaskForm.to(formValue))
      .subscribe(
        c => NotificationService.translateMessage('supervision.task.action.save'),
        error => {
          this.form.enable();
          NotificationService.translateError(error);
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
  }

  private currentUserCanEdit(creatorId: number): void {
    this.currentUser.user.subscribe(current => {
      this.canEdit = Some(creatorId)
        .filter(id => NumberUtil.isDefined(id))
        .map(id => id === current.id)
        .orElse(true);
    });
  }

  private preferredSupervisor(): void {
    const app = this.applicationState.application;
    const criteria = new UserSearchCriteria(RoleType.ROLE_SUPERVISE, app.typeEnum, app.firstLocation.effectiveCityDistrictId);
    this.userHub.searchUsers(criteria).map(preferred => ArrayUtil.first(preferred))
      .filter(preferred => !!preferred)
      .subscribe(preferred => this.form.patchValue({handlerId: preferred.id}));
  }
}
