import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {CommentType} from '@model/application/comment/comment-type';
import {StatusChangeInfo} from '@model/application/status-change-info';
import {Observable} from 'rxjs';
import {User} from '@model/user/user';
import {RoleType} from '@model/user/role-type';
import {UserService} from '@service/user/user-service';
import {filter, take} from 'rxjs/operators';
import {ConfigurationHelperService} from '@service/config/configuration-helper.service';
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
  proposalForm: UntypedFormGroup;

  handlers: Observable<Array<User>>;

  constructor(@Inject(MAT_DIALOG_DATA) public data: DecisionProposalData,
              private dialogRef: MatDialogRef<DecisionProposalModalComponent>,
              private userService: UserService,
              private fb: UntypedFormBuilder,
              private configHelper: ConfigurationHelperService) {}

  ngOnInit(): void {
    this.proposalForm = this.fb.group({
      comment: [this.data.comment, Validators.required],
      handler: [undefined, Validators.required]
    });

    this.handlers = this.userService.getByRole(RoleType.ROLE_DECISION);
    this.selectDefaultDecisionMaker();
  }

  confirm() {
    const formValue = this.proposalForm.value;
    this.dialogRef.close(new StatusChangeInfo(this.data.proposalType, formValue.comment, formValue.handler));
  }

  cancel() {
    this.dialogRef.close();
  }

  private selectDefaultDecisionMaker(): void {
    this.configHelper.getDecisionMaker(this.data.applicationType).pipe(
      take(1),
      filter(user => !!user)
    ).subscribe(user => this.proposalForm.patchValue({handler: user.id}));
  }
}
