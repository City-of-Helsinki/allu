import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CommentType} from '@model/application/comment/comment-type';
import {StatusChangeInfo} from '@model/application/status-change-info';
import {Observable} from 'rxjs';
import {User} from '@model/user/user';
import {RoleType} from '@model/user/role-type';
import {UserService} from '@service/user/user-service';
import {take, tap} from 'rxjs/internal/operators';
import {ArrayUtil} from '@util/array-util';
import {ConfigurationHelperService} from '@service/config/configuration-helper.service';
import {ConfigurationKey} from '@model/config/configuration-key';
import {ApplicationType} from '@model/application/type/application-type';

export const DECISION_PROPOSAL_MODAL_CONFIG = {width: '800px'};

export interface DecisionProposalData {
  proposalType: CommentType;
  cityDistrict: number;
  applicationType: ApplicationType;
  comment?: string;
}

@Component({
  selector: 'decision-proposal-modal',
  templateUrl: './decision-proposal-modal.component.html',
  styleUrls: ['./decision-proposal-modal.component.scss']
})
export class DecisionProposalModalComponent implements OnInit {
  proposalForm: FormGroup;

  handlers: Observable<Array<User>>;

  constructor(@Inject(MAT_DIALOG_DATA) public data: DecisionProposalData,
              private dialogRef: MatDialogRef<DecisionProposalModalComponent>,
              private userService: UserService,
              private fb: FormBuilder,
              private configHelper: ConfigurationHelperService) {}

  ngOnInit(): void {
    this.proposalForm = this.fb.group({
      comment: [this.data.comment, Validators.required],
      handler: [undefined, Validators.required]
    });

    this.handlers = this.userService.getByRole(RoleType.ROLE_DECISION).pipe(
      tap(decisionMakers => this.selectDefaultDecisionMaker(decisionMakers))
    );
  }

  confirm() {
    const formValue = this.proposalForm.value;
    this.dialogRef.close(new StatusChangeInfo(this.data.proposalType, formValue.comment, formValue.handler));
  }

  cancel() {
    this.dialogRef.close();
  }

  private selectDefaultDecisionMaker(decisionMakers: User[]): void {
    this.configHelper.getSingleConfiguration(ConfigurationKey[this.data.applicationType + '_DECISION_MAKER']).pipe(
      take(1)
    ).subscribe(
        config => {
          const user = ArrayUtil.first(decisionMakers, u => u.userName === config.value);
          if (user) {
            this.proposalForm.patchValue({handler: user.id});
          }
        });
  }
}
