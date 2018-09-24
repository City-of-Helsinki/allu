import {Component, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CommentType} from '@model/application/comment/comment-type';
import {Observable} from 'rxjs';
import {User} from '@model/user/user';
import {RoleType} from '@model/user/role-type';
import {ContractApprovalInfo} from '@model/decision/contract-approval-info';
import {findTranslation} from '@util/translations';
import {UserService} from '@service/user/user-service';

export const CONTRACT_APPROVAL_MODAL_CONFIG = {width: '800px'};

type ApprovalBasisType = 'frameAgreementExists' | 'contractAsAttachment';

@Component({
  selector: 'contract-approval-modal',
  templateUrl: './contract-approval-modal.component.html'
})
export class ContractApprovalModalComponent implements OnInit {
  approvalForm: FormGroup;

  handlers: Observable<Array<User>>;

  constructor(private dialogRef: MatDialogRef<ContractApprovalModalComponent>,
              private userService: UserService,
              private fb: FormBuilder) {}

  ngOnInit(): void {
    this.approvalForm = this.fb.group({
      comment: ['', Validators.required],
      handler: [undefined, Validators.required],
      approvalBasis: [undefined, Validators.required]
    });

    this.handlers = this.userService.getByRole(RoleType.ROLE_DECISION);
  }

  confirm() {
    const formValue = this.approvalForm.value;
    const approvalBasis: ApprovalBasisType = formValue.approvalBasis;
    const approvalBasisText = findTranslation(`contract.approval.basis.${approvalBasis}`);
    const comment = `${approvalBasisText}. ${formValue.comment}`;
    const frameAgreementExists = 'frameAgreementExists' === approvalBasis;
    const contractAsAttachment = 'contractAsAttachment' === approvalBasis;
    const approvalInfo = new ContractApprovalInfo(
      CommentType.PROPOSE_APPROVAL,
      comment,
      formValue.handler,
      frameAgreementExists,
      contractAsAttachment
    );

    this.dialogRef.close(approvalInfo);
  }

  cancel() {
    this.dialogRef.close();
  }
}
