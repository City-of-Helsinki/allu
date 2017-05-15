import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup} from '@angular/forms';

import {ApplicantForm} from './applicant.form';
import {Applicant} from '../../../../model/application/applicant/applicant';
import {Some} from '../../../../util/option';
import {Subject} from 'rxjs/Subject';
import {NumberUtil} from '../../../../util/number.util';
import {MdDialog, MdDialogRef} from '@angular/material';
import {ApplicantModalComponent} from '../../../customerregistry/applicant/applicant-modal.component';
import {Contact} from '../../../../model/application/contact';
import {Observable} from 'rxjs';

const ALWAYS_ENABLED_FIELDS = ['id', 'type', 'name', 'representative'];

@Component({
  selector: 'applicant',
  viewProviders: [],
  template: require('./applicant.component.html'),
  styles: [
    require('./applicant.component.scss')
  ]
})
export class ApplicantComponent implements OnInit, OnDestroy {
  @Input() applicationForm: FormGroup;
  @Input() applicant: Applicant;
  @Input() contactList: Array<Contact> = [];
  @Input() headerText = 'Hakija';
  @Input() formName = 'applicant';
  @Input() contactHeaderText = 'Yhteyshenkilö';
  @Input() contactFormName = 'contacts';
  @Input() readonly: boolean;
  @Input() showCopyToBilling = false;
  @Input() showRepresentative = false;
  @Input() showPropertyDeveloper = false;
  @Input() propertyDeveloper = false;
  @Input() representative = false;
  @Input() addNew = false;

  applicantForm: FormGroup;
  applicantEvents$ = new Subject<Applicant>();

  private dialogRef: MdDialogRef<ApplicantModalComponent>;

  constructor(private fb: FormBuilder, private dialog: MdDialog) {
    this.applicantForm = ApplicantForm.initialForm(this.fb);
    this.applicantForm.addControl('propertyDeveloper', this.fb.control(false));
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

  onCustomerChange(customer: Applicant): void {
    if (NumberUtil.isDefined(customer.id)) {
      this.disableApplicantEdit();
    }
    this.applicantEvents$.next(customer);
  }

  get applicantEvents(): Observable<Applicant> {
    return this.applicantEvents$.asObservable();
  }

  private initForm() {
    this.applicationForm.addControl(this.formName, this.applicantForm);
    Some(this.applicant)
      .map(applicant => ApplicantForm.fromApplicant(applicant))
      .do(applicant => {
        this.applicantForm.patchValue(applicant);
        this.disableApplicantEdit();
        this.applicantEvents$.next(applicant);
      });

    this.applicantForm.patchValue({
      propertyDeveloper: this.propertyDeveloper,
      representative: this.representative
    });
  }

  private disableApplicantEdit(): void {
    Object.keys(this.applicantForm.controls)
      .filter(key => ALWAYS_ENABLED_FIELDS.indexOf(key) < 0)
      .forEach(key => this.applicantForm.get(key).disable());
  }
}
