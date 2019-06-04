import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CommentType} from '@model/application/comment/comment-type';
import {Observable} from 'rxjs';
import {User} from '@model/user/user';
import {RoleType} from '@model/user/role-type';
import {UserService} from '@service/user/user-service';
import {filter, take} from 'rxjs/operators';
import {ConfigurationHelperService} from '@service/config/configuration-helper.service';
import {ApplicationType} from '@model/application/type/application-type';
import {TerminationInfo} from '@feature/decision/termination/termination-info';

export const TERMINATION_MODAL_CONFIG = {width: '800px'};

export interface TerminationData {
  applicationType: ApplicationType;
  applicationId: number;
}

@Component({
  selector: 'termination-modal',
  templateUrl: './termination-modal.component.html'
})
export class TerminationModalComponent implements OnInit {
  terminationForm: FormGroup;

  handlers: Observable<Array<User>>;

  constructor(@Inject(MAT_DIALOG_DATA) public data: TerminationData,
              private dialogRef: MatDialogRef<TerminationModalComponent>,
              private userService: UserService,
              private fb: FormBuilder,
              private configHelper: ConfigurationHelperService
  ) {}

  ngOnInit(): void {
    this.terminationForm = this.fb.group({
      comment: ['', Validators.required],
      handler: [undefined, Validators.required],
      terminationTime: [undefined, Validators.required]
    });

    this.handlers = this.userService.getByRole(RoleType.ROLE_DECISION);
    this.selectDefaultDecisionMaker();
  }

  confirm() {
    const formValue = this.terminationForm.value;
    const terminationTime = formValue.terminationTime;
    const comment = formValue.comment;
    const terminationInfo = new TerminationInfo(
      null,
      this.data.applicationId,
      CommentType.PROPOSE_TERMINATION,
      formValue.handler,
      null,
      terminationTime,
      comment,
    );

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
