import {Component, Input, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ApplicationStatus} from '../../model/application/application-status';
import {DistributionEntry} from '../../model/common/distribution-entry';
import {DistributionEntryForm} from '../application/distribution/distribution-list/distribution-entry-form';
import {UserHub} from '../../service/user/user-hub';
import {User} from '../../model/user/user';
import {Observable} from 'rxjs/Observable';
import {RoleType} from '../../model/user/role-type';
import {ApplicationState} from '../../service/application/application-state';
import {UserSearchCriteria} from '../../model/user/user-search-criteria';
import {ArrayUtil} from '../../util/array-util';

export const DECISION_MODAL_CONFIG = {width: '800px'};

export interface DecisionConfirmation {
  status: ApplicationStatus;
  distributionList: Array<DistributionEntry>;
  emailMessage: string;
  comment: string;
  handler?: number;
}

@Component({
  selector: 'decision-modal',
  templateUrl: './decision-modal.component.html',
  styleUrls: ['./decision-modal.component.scss']
})
export class DecisionModalComponent implements OnInit {
  @Input() status: string;
  @Input() distributionList: Array<DistributionEntry> = [];

  decisionForm: FormGroup;

  handlers: Observable<Array<User>>;

  constructor(private dialogRef: MatDialogRef<DecisionModalComponent>,
              private userHub: UserHub,
              private applicationState: ApplicationState,
              private fb: FormBuilder) {}

  ngOnInit(): void {
    this.decisionForm = this.fb.group({
      comment: [''],
      emailMessage: ['']
    });

    if (ApplicationStatus.RETURNED_TO_PREPARATION === ApplicationStatus[this.status]) {
      this.handlers = this.userHub.getByRole(RoleType.ROLE_PROCESS_APPLICATION);
      this.decisionForm.addControl('handler', this.fb.control(undefined, Validators.required));
      this.preferredHandler().subscribe(preferred => this.decisionForm.patchValue({handler: preferred.id}));
    }
  }

  confirm() {
    const formValue = this.decisionForm.value;

    this.dialogRef.close({
      status: ApplicationStatus[this.status],
      distributionList: this.decisionDistribution(),
      emailMessage: formValue.emailMessage,
      comment: formValue.comment,
      handler: formValue.handler
    });
  }

  cancel() {
    this.dialogRef.close();
  }

  private decisionDistribution(): Array<DistributionEntry> {
    return this.decisionForm.value.distributionRows
      .map(d => DistributionEntryForm.to(d));
  }

  private preferredHandler(): Observable<User> {
    const app = this.applicationState.application;
    const criteria = new UserSearchCriteria(RoleType.ROLE_PROCESS_APPLICATION, app.typeEnum, app.firstLocation.effectiveCityDistrictId);
    return this.userHub.searchUsers(criteria).map(preferred => ArrayUtil.first(preferred))
      .filter(preferred => !!preferred);
  }
}
