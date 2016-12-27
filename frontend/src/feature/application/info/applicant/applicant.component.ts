import {Component, Input, OnInit, OnDestroy} from '@angular/core';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';

import {ApplicantForm} from './applicant.form';
import {translations} from '../../../../util/translations';
import {EnumUtil} from '../../../../util/enum.util';
import {ApplicantType} from '../../../../model/application/applicant/applicant-type';
import {emailValidator} from '../../../../util/complex-validator';
import {Applicant} from '../../../../model/application/applicant';
import {Some} from '../../../../util/option';

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

  applicantTypes = EnumUtil.enumValues(ApplicantType);
  applicantForm: FormGroup;
  translations = translations;

  constructor(private fb: FormBuilder) {
    this.applicantForm = this.fb.group({
      id: undefined,
      type: [undefined, Validators.required],
      representative: [undefined],
      detailsId: undefined,
      name: ['', [Validators.required, Validators.minLength(2)]],
      registryKey: ['', [Validators.required, Validators.minLength(2)]],
      country: ['Suomi'],
      postalAddress: this.fb.group({
        streetAddress: [''],
        postalCode: [''],
        city: ['']
      }),
      email: ['', emailValidator],
      phone: ['', Validators.minLength(2)]
    });
  }

  ngOnInit(): void {
    this.initForm();
  }

  ngOnDestroy(): void {
    this.applicationForm.removeControl(this.formName);
  }

  private initForm() {
    this.applicationForm.addControl(this.formName, this.applicantForm);

    Some(this.applicant)
      .map(applicant => ApplicantForm.fromApplicant(applicant))
      .do(applicant => this.applicantForm.patchValue(applicant));
  }
}
