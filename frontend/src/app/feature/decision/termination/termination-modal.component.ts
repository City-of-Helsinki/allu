import {Component, Inject, OnInit} from '@angular/core';
import {MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA, MatLegacyDialogRef as MatDialogRef} from '@angular/material/legacy-dialog';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from '@angular/forms';
import {CommentType} from '@model/application/comment/comment-type';
import {Observable} from 'rxjs';
import {User} from '@model/user/user';
import {RoleType} from '@model/user/role-type';
import {UserService} from '@service/user/user-service';
import {filter, take} from 'rxjs/operators';
import {ConfigurationHelperService} from '@service/config/configuration-helper.service';
import {ApplicationType} from '@model/application/type/application-type';
import {TerminationInfo} from '@feature/decision/termination/termination-info';
import {ComplexValidator} from '@util/complex-validator';

export const TERMINATION_MODAL_CONFIG = {width: '800px'};

export interface TerminationData {
  applicationType: ApplicationType;
  applicationId: number;
  termination: TerminationInfo;
  applicationStartTime: Date;
}

@Component({
  selector: 'termination-modal',
  templateUrl: './termination-modal.component.html'
})
export class TerminationModalComponent implements OnInit {
  terminationForm: UntypedFormGroup;

  existingTermination: TerminationInfo;

  handlers: Observable<Array<User>>;

  constructor(@Inject(MAT_DIALOG_DATA) public data: TerminationData,
              private dialogRef: MatDialogRef<TerminationModalComponent>,
              private userService: UserService,
              private fb: UntypedFormBuilder,
              private configHelper: ConfigurationHelperService
  ) {}

  ngOnInit(): void {
    this.existingTermination = this.data.termination || new TerminationInfo(true);
    this.terminationForm = this.fb.group({
      comment: [this.existingTermination.comment, Validators.required],
      handler: [this.existingTermination.owner, Validators.required],
      expirationTime: [this.existingTermination.expirationTime,
        [Validators.required, ComplexValidator.afterOrSameDay(this.data.applicationStartTime)]]
    });

    this.handlers = this.userService.getByRole(RoleType.ROLE_DECISION);
    if (!this.existingTermination.owner) {
      this.selectDefaultDecisionMaker();
    }
  }

  saveDraft() {
    this.terminateImpl(true);
  }

  terminate() {
    this.terminateImpl(false);
  }

  terminateImpl(draft: boolean) {
    const formValue = this.terminationForm.value;

    const terminationInfo: TerminationInfo = {...this.existingTermination};

    terminationInfo.draft = draft;
    terminationInfo.applicationId = this.data.applicationId;
    terminationInfo.type = CommentType.PROPOSE_TERMINATION;
    terminationInfo.owner = formValue.handler;
    terminationInfo.expirationTime = formValue.expirationTime;
    terminationInfo.comment = formValue.comment;

    this.dialogRef.close(terminationInfo);
  }

  cancel() {
    this.dialogRef.close();
  }

  private selectDefaultDecisionMaker(): void {
    this.configHelper.getDecisionMaker(this.data.applicationType).pipe(
      take(1),
      filter(user => !!user)
    ).subscribe(user => this.terminationForm.patchValue({handler: user.id}));
  }
}
