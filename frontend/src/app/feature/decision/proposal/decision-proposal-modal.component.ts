import {Component, Input, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CommentType} from '../../../model/application/comment/comment-type';
import {StatusChangeInfo} from '../../../model/application/status-change-info';
import {Observable} from 'rxjs';
import {User} from '../../../model/user/user';
import {UserHub} from '../../../service/user/user-hub';
import {RoleType} from '../../../model/user/role-type';

export const DECISION_PROPOSAL_MODAL_CONFIG = {width: '800px'};

@Component({
  selector: 'decision-proposal-modal',
  templateUrl: './decision-proposal-modal.component.html',
  styleUrls: ['./decision-proposal-modal.component.scss']
})
export class DecisionProposalModalComponent implements OnInit {
  @Input() proposal: string;

  proposalForm: FormGroup;

  handlers: Observable<Array<User>>;

  constructor(private dialogRef: MatDialogRef<DecisionProposalModalComponent>,
              private userHub: UserHub,
              private fb: FormBuilder) {}

  ngOnInit(): void {
    this.proposalForm = this.fb.group({
      comment: ['', Validators.required],
      handler: [undefined, Validators.required]
    });

    this.handlers = this.userHub.getByRole(RoleType.ROLE_DECISION);
  }

  confirm() {
    const formValue = this.proposalForm.value;
    this.dialogRef.close(new StatusChangeInfo(CommentType[this.proposal], formValue.comment, formValue.handler));
  }

  cancel() {
    this.dialogRef.close();
  }
}
