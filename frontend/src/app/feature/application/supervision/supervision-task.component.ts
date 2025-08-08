import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {UntypedFormGroup} from '@angular/forms';
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
import {
  SUPERVISION_APPROVAL_MODAL_CONFIG,
  SupervisionApprovalModalComponent,
  SupervisionApprovalModalData,
  SupervisionApprovalResolutionType,
  SupervisionApprovalResult
} from './supervision-approval-modal.component';
import {MatDialog, MatDialogRef} from '@angular/material/dialog';
import {SupervisionTask} from '@model/application/supervision/supervision-task';
import {filter, map, take} from 'rxjs/internal/operators';
import {Store, select} from '@ngrx/store';
import * as fromRoot from '@feature/allu/reducers';
import * as fromApplication from '@feature/application/reducers';
import {Approve, ChangeOwner, Reject, Remove, Save} from '@feature/application/supervision/actions/supervision-task-actions';
import {Application} from '@model/application/application';
import {ApplicationType} from '@model/application/type/application-type';
import {ExcavationAnnouncement} from '@model/application/excavation-announcement/excavation-announcement';
import {Some} from '@util/option';
import {SetRequiredTasks} from '@feature/application/actions/excavation-announcement-actions';
import {Observable, Subject} from 'rxjs';
import {UserService} from '@service/user/user-service';
import {DECISION_PROPOSAL_MODAL_CONFIG, DecisionProposalModalComponent} from '@feature/decision/proposal/decision-proposal-modal.component';
import {CommentType} from '@model/application/comment/comment-type';
import {ApplicationStatus} from '@model/application/application-status';
import {StatusChangeInfo} from '@model/application/status-change-info';
import {
  ExcavationSupervisionApprovalModalComponent,
  ExcavationSupervisionApprovalModalData
} from '@feature/application/supervision/excavation-supervision-approval-modal.component';
import {
  AreaRentalSupervisionApprovalModalComponent,
  AreaRentalSupervisionApprovalModalData
} from '@feature/application/supervision/area-rental-supervision-approval-modal.component';
import {Location} from '@model/common/location';
import {AreaRental} from '@model/application/area-rental/area-rental';
import {NumberUtil} from '@util/number.util';
import { LoadingIndicatorComponent } from '@app/feature/common/loading-indicator/loading-indicator.component';
import * as fromSupervisionTask from './reducers';


@Component({
  selector: 'supervision-task',
  templateUrl: './supervision-task.component.html',
  styleUrls: [
    './supervision-task.component.scss'
  ]
})
export class SupervisionTaskComponent implements OnInit, OnDestroy {
  @Input() application: Application;
  @Input() form: UntypedFormGroup;
  @Output() onRemove = new EventEmitter<void>();

  taskTypes: string[] = [];
  canEdit = false;
  canApprove = false;
  canRemove = false;
  canTakeOwnership = false;
  editing = false;
  approveDisabled = false;
  location: Location;
  locations: Location[];
  availableSupervisors: User[] = [];

  private originalEntry: SupervisionTaskForm;
  private destroy = new Subject<boolean>();
  private saving$: Observable<boolean>;
  private loadingDialog: MatDialogRef<LoadingIndicatorComponent, any>;

  constructor(private applicationStore: ApplicationStore,
              private store: Store<fromRoot.State>,
              private currentUser: CurrentUser,
              private userService: UserService,
              private dialog: MatDialog) {
  }

  @Input() set hasDisablingTags(disablingTags: boolean) {
    const disabledStatus = ApplicationStatus.DECISIONMAKING === this.application.status;
    const disabledTaskType = [SupervisionTaskType.OPERATIONAL_CONDITION, SupervisionTaskType.FINAL_SUPERVISION]
      .indexOf(this.form.value.type) >= 0;
    const disabledByOperationalCondition = this.form.value.type === SupervisionTaskType.OPERATIONAL_CONDITION &&
      this.application.status !== ApplicationStatus.DECISION;
    const disabledByFinalSupervision = this.form.value.type === SupervisionTaskType.FINAL_SUPERVISION &&
      !ArrayUtil.contains([ApplicationStatus.DECISION, ApplicationStatus.OPERATIONAL_CONDITION, ApplicationStatus.FINISHED],
        this.application.status);

    this.approveDisabled = (disabledTaskType && (disabledStatus || disablingTags)) ||
      disabledByOperationalCondition || disabledByFinalSupervision;
  }

  ngOnInit(): void {
    const formValue = this.form.value;
    if (formValue.id) {
      this.form.disable();
      this.editing = false;
    } else {
      this.editing = true;
      this.preferredSupervisor();
    }
    if (formValue.automatic) {
      this.taskTypes = EnumUtil.enumValues(SupervisionTaskType);
    } else {
      this.taskTypes = EnumUtil.enumValues(SupervisionTaskType)
        .filter(type => !isAutomaticSupervisionTaskType(SupervisionTaskType[type]));
    }

    this.currentUserCanEdit(formValue.creatorId, formValue.status);
    this.currentUserCanApprove(formValue.ownerId, formValue.status);
    this.currentUserCanTakeOwnership(formValue.ownerId, formValue.status);
    this.userCanRemove(formValue.status);
    this.locations = this.getLocations();
    this.location = this.getLocation(this.locations, formValue.locationId);

    this.saving$ = this.store.pipe(
      select(fromSupervisionTask.getSaving),
    );

    this.createModalListener();

  }

  ngOnDestroy(): void {
    this.destroy.next(true);
    this.destroy.unsubscribe();
  }

  @Input() set supervisors(users: User[]) {
    const formValue = this.form.value;
    if ([SupervisionTaskStatusType.APPROVED, SupervisionTaskStatusType.REJECTED].indexOf(formValue.status) >= 0) {
      this.availableSupervisors = [new User(formValue.ownerId, undefined, formValue.ownerName)];
    } else {
      this.availableSupervisors = users;
    }
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
    ).subscribe(task => this.handleSave(task));
  }

  cancel(): void {
    // Reset form to original value when such exists,
    // otherwise treat as remove
    if (this.originalEntry) {
      this.form.patchValue(this.originalEntry);
      this.originalEntry = undefined;
      this.form.disable();
      this.editing = false;
    } else {
      this.onRemove.emit();
    }
  }

  edit(): void {
    this.editing = true;
    this.form.enable();
    this.originalEntry = this.form.value;
    if (this.originalEntry.automatic || this.originalEntry.id) {
      this.form.controls['type'].disable();
    }
  }

  private createModalListener(): void {
    const closeGlassDialog = (): void => {
      if (this.loadingDialog) {
        this.loadingDialog.close();
      }
    };
    this.saving$.subscribe(value => {
      value ? this.openGlassDialog() : closeGlassDialog();
    });
  }

  private openGlassDialog(): void {
    this.loadingDialog = this.dialog.open(LoadingIndicatorComponent, {
      disableClose: true,
      hasBackdrop: true,
      backdropClass: 'custom-backdrop'
     });
  }

  approve(): void {
    this.openModal(SupervisionApprovalResolutionType.APPROVE).afterClosed().pipe(
      filter(result => !!result),
    ).subscribe((result: SupervisionApprovalResult) => this.approveWithState(result));
  }

  reject(): void {
    this.openModal(SupervisionApprovalResolutionType.REJECT).afterClosed().pipe(
      filter(result => !!result)
    ).subscribe(result => this.store.dispatch(
      new Reject(this.taskWithResult(SupervisionTaskStatusType.REJECTED, result), result.newSupervisionDate)
    ));
  }

  moveToSelf(taskId: number): void {
    this.currentUser.user.pipe(take(1))
      .subscribe(user => this.store.dispatch(new ChangeOwner({ownerId: user.id, taskIds: [taskId]})));
  }

  private approveWithState(result: SupervisionApprovalResult): void {
    if (ApplicationStatus.DECISIONMAKING === result.statusChange) {
      this.toDecisionMaking(result.result).subscribe(changeInfo => this.handleApproval(result, changeInfo));
    } else {
      this.handleApproval(result);
    }
  }

  private handleApproval(result: SupervisionApprovalResult, changeInfo?: StatusChangeInfo) {
    const task = this.taskWithResult(SupervisionTaskStatusType.APPROVED, result);
    this.store.dispatch(new Approve({
      task,
      reportedDate: result.reportedDate,
      status: result.statusChange,
      changeInfo
    }));
    Some(result.requiredTasks).do(tasks => this.store.dispatch(new SetRequiredTasks(tasks)));
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
    const task = SupervisionTaskForm.to(this.form.value);
    switch (this.application.type) {
      case ApplicationType.EXCAVATION_ANNOUNCEMENT: {
        return this.openExcavationApprovalModal(type, task);
      }

      case ApplicationType.AREA_RENTAL: {
        return this.openAreaRentalApprovalModal(type, task);
      }

      default: {
        return this.openApprovalModal(type, task);
      }
    }
  }

  private openApprovalModal(type: SupervisionApprovalResolutionType, task: SupervisionTask):
    MatDialogRef<SupervisionApprovalModalComponent> {
    const config = {
      ...SUPERVISION_APPROVAL_MODAL_CONFIG,
      data: this.approvalModalData(type, task)
    };

    return this.dialog.open(SupervisionApprovalModalComponent, config);
  }

  private openExcavationApprovalModal(type: SupervisionApprovalResolutionType, task: SupervisionTask):
    MatDialogRef<SupervisionApprovalModalComponent> {
    const baseData = this.approvalModalData(type, task);

    const config = {
      ...SUPERVISION_APPROVAL_MODAL_CONFIG,
      data: this.excavationApprovalModalData(baseData)
    };

    return this.dialog.open(ExcavationSupervisionApprovalModalComponent, config);
  }

  private openAreaRentalApprovalModal(type: SupervisionApprovalResolutionType, task: SupervisionTask):
    MatDialogRef<SupervisionApprovalModalComponent> {

    const baseData = this.approvalModalData(type, task);

    const config = {
      ...SUPERVISION_APPROVAL_MODAL_CONFIG,
      data: this.areaRentalApprovalModalData(baseData)
    };

    return this.dialog.open(AreaRentalSupervisionApprovalModalComponent, config);
  }

  private currentUserCanEdit(creatorId: number, status: SupervisionTaskStatusType): void {
    this.currentUser.user.pipe(take(1)).subscribe(current => {
      const createdByCurrent = creatorId === current.id;
      const isAdmin = current.hasRole(RoleType.ROLE_ADMIN);
      const editableStatus = SupervisionTaskStatusType.APPROVED !== status;
      this.canEdit = (!NumberUtil.isDefined(creatorId) || isAdmin || createdByCurrent) && editableStatus;
    });
  }

  private currentUserCanApprove(ownerId: number, status: SupervisionTaskStatusType): void {
    this.currentUser.isCurrentUser(ownerId).pipe(take(1)).subscribe(isCurrent => {
      this.canApprove = isCurrent && SupervisionTaskStatusType.OPEN === status;
    });
  }

  private currentUserCanTakeOwnership(ownerId: number, status: SupervisionTaskStatusType): void {
    this.currentUser.user.pipe(take(1)).subscribe(currentUser => {
      const ownedByOther = ownerId !== currentUser.id;
      const isSupervisor = currentUser.assignedRoles.indexOf(RoleType.ROLE_SUPERVISE) > -1;
      this.canTakeOwnership = ownedByOther && isSupervisor && status === SupervisionTaskStatusType.OPEN;
    });
  }

  private userCanRemove(status: SupervisionTaskStatusType): void {
    this.canRemove = status === undefined || SupervisionTaskStatusType.OPEN === status;
  }

  private preferredSupervisor(): void {
    const app = this.application;
    const criteria = new UserSearchCriteria(RoleType.ROLE_SUPERVISE, app.type, app.firstLocation.effectiveCityDistrictId);
    this.userService.search(criteria).pipe(
      map(preferred => ArrayUtil.first(preferred)),
      filter(preferred => !!preferred)
    ).subscribe(preferred => this.form.patchValue({ownerId: preferred.id}));
  }

  private approvalModalData(resolutionType: SupervisionApprovalResolutionType, task: SupervisionTask): SupervisionApprovalModalData {
    return {
      resolutionType: resolutionType,
      taskType: task.type,
      application: this.application
    };
  }

  private excavationApprovalModalData(baseData: SupervisionApprovalModalData): ExcavationSupervisionApprovalModalData {
    let reportedDate: Date;
    let comparedDate: Date;

    const extension = <ExcavationAnnouncement> baseData.application.extension;
    if (SupervisionTaskType.OPERATIONAL_CONDITION === baseData.taskType) {
      reportedDate = extension.customerWinterTimeOperation || extension.winterTimeOperation;
      comparedDate = extension.winterTimeOperation;
    } else if (SupervisionTaskType.FINAL_SUPERVISION === baseData.taskType) {
      reportedDate = extension.customerWorkFinished || baseData.application.endTime;
      comparedDate = baseData.application.endTime;
    }

    return {
      ...baseData,
      reportedDate,
      comparedDate,
      minDate: baseData.application.startTime,
      compactionAndBearingCapacityMeasurement: extension.compactionAndBearingCapacityMeasurement,
      qualityAssuranceTest: extension.qualityAssuranceTest
    };
  }

  private areaRentalApprovalModalData(baseData: SupervisionApprovalModalData): AreaRentalSupervisionApprovalModalData {
    let reportedDate: Date;
    let comparedDate: Date;
    const minDate = this.findLatestStartingDate();
    const lastAreaEndDate = this.findLatestEndDate();

    if (SupervisionTaskType.FINAL_SUPERVISION === baseData.taskType) {
      const extension = <AreaRental> baseData.application.extension;
      reportedDate = extension.customerWorkFinished || baseData.application.endTime;
      comparedDate = baseData.application.endTime;
    }

    return {
      ...baseData,
      reportedDate,
      comparedDate,
      minDate,
      lastAreaEndDate
    };
  }

  private handleSave(task: SupervisionTask): void {
    this.store.dispatch(new Save(task));
    this.editing = false;
  }

  private toDecisionMaking(comment?: string): Observable<StatusChangeInfo> {
    const config = {
      ...DECISION_PROPOSAL_MODAL_CONFIG,
      data: {
        proposalType: CommentType.PROPOSE_APPROVAL,
        cityDistrict: this.application.firstLocation.effectiveCityDistrictId,
        applicationType: this.application.type,
        comment
      }
    };

    return this.dialog.open<DecisionProposalModalComponent>(DecisionProposalModalComponent, config).afterClosed().pipe(
      filter(result => !!result)
    );
  }

  private getLocations(): Location[] {
    return !ArrayUtil.empty(this.form.value.supervisedLocations)
      ? this.form.value.supervisedLocations
      : this.application?.locations;
  }

  private getLocation(locations: Location[], locationId: number): Location {
    return ArrayUtil.first(locations, l => l.id === locationId);
  }

  private findLatestStartingDate(): Date {
    return this.application.locations.reduce((d, l) => d > l.startTime ? d : l.startTime, new Date(0));
  }

  private findLatestEndDate(): Date {
    return this.application.locations.reduce((d, l) => d > l.endTime ? d : l.endTime, new Date(0));
  }
}
