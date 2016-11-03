import {Component, Input, OnInit, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, FormControl, Validators} from '@angular/forms';

import {Applicant} from '../../../model/application/applicant';
import {StructureMeta} from '../../../model/application/structure-meta';
import {ApplicationHub} from '../../../service/application/application-hub';
import {ApplicantForm} from './applicant.form';
import {Application} from '../../../model/application/application';
import {translations} from '../../../util/translations';
import {EnumUtil} from '../../../util/enum.util';
import {ApplicantType} from '../../../model/application/applicant/applicant-type';

@Component({
  selector: 'applicant',
  viewProviders: [],
  template: require('./applicant.component.html'),
  styles: []
})
export class ApplicantComponent implements OnInit, AfterViewInit {
  @Input() applicationForm: FormGroup;
  @Input() readonly: boolean;

  applicantTypes = EnumUtil.enumValues(ApplicantType);
  applicantForm: FormGroup;
  meta: StructureMeta;
  translations = translations;

  constructor(private applicationHub: ApplicationHub, private fb: FormBuilder, private route: ActivatedRoute) {
    applicationHub.metaData().subscribe(meta => this.metadataLoaded(meta));
  }

  ngOnInit(): void {
    this.initForm();
  }

  ngAfterViewInit(): void {
  }

  private initForm() {
    this.applicantForm = this.fb.group({
      id: undefined,
      type: [undefined, Validators.required],
      representative: [false],
      detailsId: undefined,
      name: ['', [Validators.required, Validators.minLength(2)]],
      identifier: ['', [Validators.required, Validators.minLength(2)]],
      country: ['Suomi'],
      postalAddress: this.fb.group({
        streetAddress: [''],
        postalCode: [''],
        city: ['']
      }),
      email: ['', Validators.pattern('.+@.+\\..+')],
      phone: ['', Validators.minLength(2)]
    });

    this.applicationForm.addControl('applicant', this.applicantForm);

    this.route.parent.data
      .map((data: {application: Application}) => data.application.applicant)
      .filter(applicant => !!applicant)
      .subscribe(applicant => {
        this.applicantForm.patchValue(ApplicantForm.fromApplicant(applicant));
      });
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.meta = metadata;
  }
}
