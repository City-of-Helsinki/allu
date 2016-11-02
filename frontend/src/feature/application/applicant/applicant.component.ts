import {Component, Input, OnInit, AfterViewInit} from '@angular/core';
import {Router, ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, FormControl, Validators} from '@angular/forms';

import {Applicant} from '../../../model/application/applicant';
import {StructureMeta} from '../../../model/application/structure-meta';
import {outdoorEventConfig, applicantIdSelection, applicantNameSelection} from '../outdoor-event/outdoor-event-config';
import {ApplicationHub} from '../../../service/application/application-hub';
import {ApplicantForm} from './applicant.form';
import {Application} from '../../../model/application/application';
import {translations} from '../../../util/translations';

@Component({
  selector: 'applicant',
  viewProviders: [],
  template: require('./applicant.component.html'),
  styles: []
})
export class ApplicantComponent implements OnInit, AfterViewInit {
  @Input() applicationForm: FormGroup;
  @Input() readonly: boolean;

  applicantForm: FormGroup;
  meta: StructureMeta;
  applicantType: any;
  applicantText: any;
  applicantNameSelection: string;
  applicantIdSelection: string;
  translations = translations;

  constructor(private applicationHub: ApplicationHub, private fb: FormBuilder, private route: ActivatedRoute) {
    applicationHub.metaData().subscribe(meta => this.metadataLoaded(meta));
  }

  ngOnInit(): void {
    this.applicantType = outdoorEventConfig.applicantType;
    this.applicantText = outdoorEventConfig.applicantText;

    this.initForm();
  }

  ngAfterViewInit(): void {
  }

  applicantTypeSelection(value: string) {
    this.applicantNameSelection = this.applicantText[value].name;
    this.applicantIdSelection = this.applicantText[value].id;
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

    this.route.parent.data.subscribe((data: {application: Application}) => {
      let applicant = data.application.applicant;

      this.applicantNameSelection = applicantNameSelection(applicant.type);
      this.applicantIdSelection = applicantIdSelection(applicant.type);

      this.applicantForm.patchValue(ApplicantForm.fromApplicant(applicant));
    });
  }

  private metadataLoaded(metadata: StructureMeta) {
    this.meta = metadata;
    this.applicantText = {
      'DEFAULT': {
        name: 'Hakijan nimi',
        id: this.meta.getUiName('applicant.businessId')},
      'COMPANY': {
        name: this.meta.getUiName('applicant.companyName'),
        id: this.meta.getUiName('applicant.businessId')},
      'ASSOCIATION': {
        name: this.meta.getUiName('applicant.organizationName'),
        id: this.meta.getUiName('applicant.businessId')},
      'PERSON': {
        name: this.meta.getUiName('applicant.personName'),
        id: this.meta.getUiName('applicant.ssn')}
    };
  }
}
