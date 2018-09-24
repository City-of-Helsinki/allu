import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ApplicationStatus} from '@model/application/application-status';
import {DistributionEntry} from '@model/common/distribution-entry';
import {DistributionEntryForm} from '@feature/application/distribution/distribution-list/distribution-entry-form';
import {User} from '@model/user/user';
import {Observable} from 'rxjs';
import {RoleType} from '@model/user/role-type';
import {ApplicationStore} from '@service/application/application-store';
import {UserSearchCriteria} from '@model/user/user-search-criteria';
import {ArrayUtil} from '@util/array-util';
import {DistributionType} from '@model/common/distribution-type';
import {filter, map} from 'rxjs/internal/operators';
import {UserService} from '@service/user/user-service';

export type DecisionModalType = 'DECISIONMAKING' | 'RETURNED_TO_PREPARATION' | 'REJECTED' | 'RESEND_EMAIL';

interface DecisionModalData {
  type: DecisionModalType;
  status: ApplicationStatus;
  distributionList: Array<DistributionEntry>;
  distributionType: DistributionType;
}

export const DECISION_MODAL_CONFIG = {
  width: '800px',
  data: {
    type: undefined,
    status: undefined,
    distributionList: [],
    distributionType: DistributionType.EMAIL
  }
};

export interface DecisionConfirmation {
  status: ApplicationStatus;
  distributionList: Array<DistributionEntry>;
  emailMessage: string;
  comment: string;
  owner?: number;
}

@Component({
  selector: 'decision-modal',
  templateUrl: './decision-modal.component.html',
  styleUrls: ['./decision-modal.component.scss']
})
export class DecisionModalComponent implements OnInit {
  status: string;
  type: string;
  distributionList: Array<DistributionEntry>;
  emailDistribution: boolean;
  ownerSelection: boolean;

  decisionForm: FormGroup;

  owners: Observable<Array<User>>;

  constructor(private dialogRef: MatDialogRef<DecisionModalComponent>,
              private userService: UserService,
              private applicationStore: ApplicationStore,
              @Inject(MAT_DIALOG_DATA) public data: DecisionModalData,
              private fb: FormBuilder) {}

  ngOnInit(): void {
    this.decisionForm = this.fb.group({
      comment: [''],
      emailMessage: ['']
    });

    this.status = ApplicationStatus[this.data.status];
    this.type = this.data.type;
    this.distributionList = this.data.distributionList;
    this.emailDistribution = DistributionType.EMAIL === this.data.distributionType
      && this.data.status !== ApplicationStatus.RETURNED_TO_PREPARATION;
    this.ownerSelection = DistributionType.PAPER === this.data.distributionType
      || this.data.status === ApplicationStatus.RETURNED_TO_PREPARATION;

    this.initOwners(this.ownerSelection);
  }

  confirm() {
    const formValue = this.decisionForm.value;

    this.dialogRef.close({
      status: ApplicationStatus[this.status],
      distributionList: this.decisionDistribution(),
      emailMessage: formValue.emailMessage,
      comment: formValue.comment,
      owner: formValue.owner
    });
  }

  cancel() {
    this.dialogRef.close();
  }

  private decisionDistribution(): Array<DistributionEntry> {
    const rows = this.decisionForm.value.distributionRows || [];
    return rows.map(d => DistributionEntryForm.to(d));
  }

  private initOwners(ownerSelection: boolean): void {
    if (ownerSelection) {
      this.owners = this.userService.getByRole(RoleType.ROLE_PROCESS_APPLICATION);
      this.decisionForm.addControl('owner', this.fb.control(undefined, Validators.required));
        this.preferredOwner().subscribe(preferred => this.decisionForm.patchValue({owner: preferred.id}));
    }
  }

  private preferredOwner(): Observable<User> {
    const app = this.applicationStore.snapshot.application;
    const criteria = new UserSearchCriteria(RoleType.ROLE_PROCESS_APPLICATION, app.type, app.firstLocation.effectiveCityDistrictId);
    return this.userService.search(criteria).pipe(
      map(preferred => ArrayUtil.first(preferred)),
      filter(preferred => !!preferred)
    );
  }
}
