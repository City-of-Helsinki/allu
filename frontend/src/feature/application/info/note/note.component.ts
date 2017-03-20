import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, Validators} from '@angular/forms';

import {ComplexValidator} from '../../../../util/complex-validator';
import {ApplicationState} from '../../../../service/application/application-state';
import {NoteForm} from './note.form';
import {ApplicantForm} from '../applicant/applicant.form';
import {ApplicationInfoBaseComponent} from '../application-info-base.component';

@Component({
  selector: 'note',
  viewProviders: [],
  template: require('./note.component.html'),
  styles: []
})
export class NoteComponent extends ApplicationInfoBaseComponent implements OnInit {

  constructor(route: ActivatedRoute,
              applicationState: ApplicationState,
              private fb: FormBuilder) {
    super(route, applicationState);
  };

  ngOnInit(): any {
    super.ngOnInit();
    this.applicationForm.patchValue(NoteForm.from(this.application));
  }

  protected update(form: NoteForm) {
    let application = super.update(form);
    application.name = form.name;
    application.uiStartTime = form.validityTimes.startTime;
    application.uiEndTime = form.validityTimes.endTime;
    application.applicant = ApplicantForm.toApplicant(form.applicant);
    application.contactList = form.contacts;
    application.extension = NoteForm.to(form);

    application.location.startTime = application.startTime;
    application.location.endTime = application.endTime;

    return application;
  }

  protected initForm() {
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
