import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {CommentType} from '@model/application/comment/comment-type';
import {Observable} from 'rxjs';
import {User} from '@model/user/user';
import {RoleType} from '@model/user/role-type';
import {ContractApprovalInfo} from '@model/decision/contract-approval-info';
import {findTranslation} from '@util/translations';
import {UserService} from '@service/user/user-service';
import {filter, take} from 'rxjs/operators';
import {ConfigurationHelperService} from '@service/config/configuration-helper.service';
import {ApplicationType} from '@model/application/type/application-type';

export const CONTRACT_APPROVAL_MODAL_CONFIG = {width: '800px'};

type ApprovalBasisType = 'frameAgreementExists' | 'contractAsAttachment';

export interface ContractApprovalData {
  applicationType: ApplicationType;
}

@Component({
  selector: 'contract-approval-modal',
  templateUrl: './contract-approval-modal.component.html'
})
export class ContractApprovalModalComponent implements OnInit {
  approvalForm: UntypedFormGroup;

  handlers: Observable<Array<User>>;

  constructor(@Inject(MAT_DIALOG_DATA) public data: ContractApprovalData,
              private dialogRef: MatDialogRef<ContractApprovalModalComponent>,
              private userService: UserService,
              private fb: UntypedFormBuilder,
              private configHelper: ConfigurationHelperService) {}

  ngOnInit(): void {
    this.approvalForm = this.fb.group({
      comment: ['', Validators.required],
      handler: [undefined, Validators.required],
      approvalBasis: [undefined, Validators.required]
    });

    this.handlers = this.userService.getByRole(RoleType.ROLE_DECISION);
    this.selectDefaultDecisionMaker();
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

  private selectDefaultDecisionMaker(): void {
    this.configHelper.getDecisionMaker(this.data.applicationType).pipe(
      take(1),
      filter(user => !!user)
    ).subscribe(user => this.approvalForm.patchValue({handler: user.id}));
  }
}
