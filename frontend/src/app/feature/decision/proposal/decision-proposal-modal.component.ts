import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CommentType} from '@model/application/comment/comment-type';
import {StatusChangeInfo} from '@model/application/status-change-info';
import {Observable} from 'rxjs';
import {User} from '@model/user/user';
import {RoleType} from '@model/user/role-type';
import {UserService} from '@service/user/user-service';
import {tap} from 'rxjs/internal/operators';
import {ArrayUtil} from '@util/array-util';

export const DECISION_PROPOSAL_MODAL_CONFIG = {width: '800px'};

export interface DecisionProposalData {
  proposalType: string;
  cityDistrict: number;
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
              private fb: FormBuilder) {}

  ngOnInit(): void {
    this.proposalForm = this.fb.group({
      comment: ['', Validators.required],
      handler: [undefined, Validators.required]
    });

    this.handlers = this.userService.getByRole(RoleType.ROLE_DECISION).pipe(
      tap(decisionMakers => this.selectDefaultDecisionMaker(decisionMakers))
    );
  }

  confirm() {
    const formValue = this.proposalForm.value;
    this.dialogRef.close(new StatusChangeInfo(CommentType[this.data.proposalType], formValue.comment, formValue.handler));
  }

  cancel() {
    this.dialogRef.close();
  }

  private selectDefaultDecisionMaker(decisionMakers: User[]): void {
    const user = ArrayUtil.first(decisionMakers, (dm) => dm.cityDistrictIds.some(districtId => districtId === this.data.cityDistrict));
    if (user) {
      this.proposalForm.patchValue({
        handler: user.id
      });
    }
  }
}
