import {Component, Input, OnInit, OnDestroy} from '@angular/core';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';

import {ApplicantForm} from './applicant.form';
import {EnumUtil} from '../../../../util/enum.util';
import {ApplicantType} from '../../../../model/application/applicant/applicant-type';
import {emailValidator, postalCodeValidator} from '../../../../util/complex-validator';
import {Applicant} from '../../../../model/application/applicant/applicant';
import {Some} from '../../../../util/option';
import {Subject} from 'rxjs/Subject';
import {CustomerHub} from '../../../../service/customer/customer-hub';
import {NotificationService} from '../../../../service/notification/notification.service';
import {NumberUtil} from '../../../../util/number.util';
import {MdDialog, MdDialogRef} from '@angular/material';
import {ApplicantModalComponent} from '../../../customerregistry/applicant/applicant-modal.component';

const ALWAYS_ENABLED_FIELDS = ['id', 'type', 'name'];

@Component({
  selector: 'applicant',
  viewProviders: [],
  template: require('./applicant.component.html'),
  styles: []
})
export class ApplicantComponent implements OnInit, OnDestroy {
  @Input() applicationForm: FormGroup;
  @Input() applicant: Applicant;
  @Input() readonly: boolean;
  @Input() headerText = 'Hakija';
  @Input() formName = 'applicant';
  @Input() showCopyToBilling = false;
  @Input() showRepresentative = false;
  @Input() showPropertyDeveloper = false;
  @Input() propertyDeveloper = false;
  @Input() representative = false;

  applicantTypes = EnumUtil.enumValues(ApplicantType);
  applicantForm: FormGroup;
  nameSearch = new Subject<Array<Applicant>>();
  nameSearchResults = this.nameSearch.asObservable();

  private dialogRef: MdDialogRef<ApplicantModalComponent>;

  constructor(private fb: FormBuilder, private dialog: MdDialog, private customerHub: CustomerHub) {
    this.applicantForm = ApplicantForm.initialForm(this.fb);
  }

  ngOnInit(): void {
    this.initForm();

    if (this.readonly) {
      this.applicantForm.disable();
    }
  }

  ngOnDestroy(): void {
    this.applicationForm.removeControl(this.formName);
  }

  onNameSearchChange(applicantType: string, term: string): void {
    this.resetFormIfExisting();
    this.customerHub.searchApplicantsBy({name: term, type: applicantType})
      .debounceTime(300)
      .subscribe(applicants => this.nameSearch.next(applicants));
  }

  applicantSelected(applicant: Applicant): void {
    this.applicantForm.patchValue(ApplicantForm.fromApplicant(applicant));
    this.disableApplicantEdit();
  }

  canBeEdited(): boolean {
    return NumberUtil.isDefined(this.applicantForm.value.id) && !this.readonly;
  }

  edit(): void {
    this.dialogRef = this.dialog.open(ApplicantModalComponent, {disableClose: false, width: '800px'});
    this.dialogRef.componentInstance.applicantId = this.applicantForm.value.id;
    this.dialogRef.afterClosed()
      .filter(applicant => !!applicant)
      .subscribe(applicant => this.applicantForm.patchValue(ApplicantForm.fromApplicant(applicant)));
  }

  private initForm() {
    this.applicationForm.addControl(this.formName, this.applicantForm);

    Some(this.applicant)
      .map(applicant => ApplicantForm.fromApplicant(applicant))
      .do(applicant => {
        this.applicantForm.patchValue(applicant);
        this.disableApplicantEdit();
      });

    this.applicantForm.patchValue({
      propertyDeveloper: this.propertyDeveloper,
      representative: this.representative
    });
  }

  /**
   * Resets form values if form contained existing applicant
   */
  private resetFormIfExisting(): void {
    if (NumberUtil.isDefined(this.applicantForm.value.id)) {
      this.applicantForm.reset({name: this.applicantForm.value.name, type: this.applicantForm.value.type});
      this.applicantForm.enable();
    }
  }

  private disableApplicantEdit(): void {
    Object.keys(this.applicantForm.controls)
      .filter(key => ALWAYS_ENABLED_FIELDS.indexOf(key) < 0)
      .forEach(key => this.applicantForm.get(key).disable());
  }
}
