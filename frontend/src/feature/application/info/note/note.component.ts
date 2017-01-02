import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';

import {Application} from '../../../../model/application/application';
import {PICKADATE_PARAMETERS} from '../../../../util/time.util';
import {UrlUtil} from '../../../../util/url.util';
import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicationState} from '../../../../service/application/application-state';
import {NoteForm} from './note.form';
import {ApplicantForm} from '../applicant/applicant.form';


@Component({
  selector: 'note',
  viewProviders: [],
  template: require('./note.component.html'),
  styles: []
})
export class NoteComponent implements OnInit {

  path: string;
  application: Application;
  applicationForm: FormGroup;
  submitPending = false;
  pickadateParams = PICKADATE_PARAMETERS;
  readonly: boolean;

  constructor(private route: ActivatedRoute,
              private fb: FormBuilder,
              private applicationState: ApplicationState) {
  };

  ngOnInit(): any {
    this.initForm();

    this.route.parent.data
      .map((data: {application: Application}) => data.application)
      .subscribe(application => {
        this.application = application;
        this.application.type = this.route.routeConfig.path;

        UrlUtil.urlPathContains(this.route.parent, 'summary').forEach(summary => {
          this.readonly = summary;
        });

        this.applicationForm.patchValue(NoteForm.from(application));

        if (this.readonly) {
          this.applicationForm.disable();
        }
      });
  }

  ngOnDestroy(): any {
  }

  onSubmit(form: NoteForm) {
    this.submitPending = true;
    let application = this.application;
    application.name = form.name;
    application.uiStartTime = form.validityTimes.startTime;
    application.uiEndTime = form.validityTimes.endTime;
    application.applicant = ApplicantForm.toApplicant(form.applicant);
    application.contactList = form.contacts;
    application.extension = NoteForm.to(form);

    this.applicationState.save(application)
      .subscribe(app => this.submitPending = false, err => this.submitPending = false);
  }

  private initForm() {
    this.applicationForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      validityTimes: this.fb.group({
        startTime: ['', Validators.required],
        endTime: ['']
      }, ComplexValidator.startBeforeEnd('startTime', 'endTime')),
      reoccurring: [false],
      description: ['']
    });
  }
}
