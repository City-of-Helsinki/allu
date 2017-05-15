import {Component, OnInit, Input} from '@angular/core';
import {MdDialogRef} from '@angular/material';
import {CustomerHub} from '../../../service/customer/customer-hub';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ApplicantForm} from '../../application/info/applicant/applicant.form';
import {findTranslation} from '../../../util/translations';
import {NotificationService} from '../../../service/notification/notification.service';
import {ApplicantType} from '../../../model/application/applicant/applicant-type';
import {EnumUtil} from '../../../util/enum.util';

@Component({
  selector: 'applicant-modal',
  template: require('./applicant-modal.component.html'),
  styles: [
    require('./applicant-modal.component.scss')
  ]
})
export class ApplicantModalComponent implements OnInit {
  @Input() applicantId: number;

  applicantForm: FormGroup;

  constructor(public dialogRef: MdDialogRef<ApplicantModalComponent>,
              private fb: FormBuilder,
              private customerHub: CustomerHub) {
    this.applicantForm = ApplicantForm.initialForm(fb);
  }

  ngOnInit(): void {
    this.customerHub.findApplicantById(this.applicantId)
      .subscribe(applicant => this.applicantForm.patchValue(ApplicantForm.fromApplicant(applicant)));
  }

  onSubmit(applicantForm: ApplicantForm) {
    let applicant = ApplicantForm.toApplicant(applicantForm);
    this.customerHub.saveApplicantWithContacts(applicant.id, applicant, [])
      .subscribe(
        saved => {
          NotificationService.message(findTranslation('applicant.action.save'));
          this.dialogRef.close(saved.applicant);
        }, error => NotificationService.error(error));
  }

  cancel() {
    this.dialogRef.close(undefined);
  }
}
